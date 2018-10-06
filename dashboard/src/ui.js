// Import ipc
ipc = require('electron').ipcRenderer;

/* DEFINE UI ELEMENTS BEFORE WRITING LISTENERS */
let ui = {
    timer: document.getElementById('timer'),
    robotState: document.getElementById('robot-state').firstChild,
    gyroConnection: document.getElementById('gyro-connection'),
    gyro: {
        container: document.getElementById('gyro'),
        val: 0,
        offset: 0,
        visualVal: 0,
        arm: document.getElementById('gyro-arm'),
        number: document.getElementById('gyro-number')
    },
    camera: document.getElementById('camera'),
    robotDiagram: {
        arm: document.getElementById('robot-arm')
    },
    autoSelect: document.getElementById('auto-select'),
    leftDrivePowa: document.getElementById('left-drive-powa'),
    rightDrivePowa: document.getElementById('right-drive-powa')
};


/**
 * Function to be called when robot connects
 * @param {boolean} connected
 */
function onRobotConnection(connected) {
    var state = connected ? 'Robot connected!' : 'Robot disconnected.';
    console.log(state);
    ui.robotState.data = state;
    if (connected) {
        // On connect hide the connect popup
        document.body.classList.toggle('login-close', true);
    }
    else {
        // On disconnect show the connect popup
        document.body.classList.toggle('login-close', false);
        // Add Enter key handler
        address.onkeydown = ev => {
            if (ev.key === 'Enter') {
                connect.click();
            }
        };
        // Enable the input and the button
        address.disabled = false;
        connect.disabled = false;
        connect.firstChild.data = 'Connect';
        // Add the default address and select xxxx
        address.value = 'roborio-xxxx.local';
        address.focus();
        address.setSelectionRange(8, 12);
        // On click try to connect and disable the input and the button
        connect.onclick = () => {
            ipc.send('connect', address.value);
            address.disabled = true;
            connect.disabled = true;
            connect.firstChild.data = 'Connecting';
        };
    }
}

/* KEY LISTENERS */

/* Unnecessary?
NetworkTables.addKeyListener('/SmartDashboard/IMU_Connected', (key, value) => {
    ui.gyroConnection = '' + value;
});
*/

// Gyro rotation
let updateGyro = (key, value) => {
    ui.gyro.val = value;
    ui.gyro.visualVal = Math.floor(ui.gyro.val - ui.gyro.offset);
    if (ui.gyro.visualVal < 0) {
        ui.gyro.visualVal += 360;
    }
    ui.gyro.arm.style.transform = `rotate(${ui.gyro.visualVal}deg)`;
    ui.gyro.number.innerHTML = ui.gyro.visualVal + 'º';
};
NetworkTables.addKeyListener('/SmartDashboard/IMU_Yaw', updateGyro);

// Left motor display
NetworkTables.addKeyListener('/SmartDashboard/Left Drive Power', (key, value) => {
    ui.leftDrivePowa.value = '' + Math.round(value * 100);
});

// Right motor display
NetworkTables.addKeyListener('/SmartDashboard/Right Drive Power', (key, value)=> {
    ui.rightDrivePowa.innerHTML = '' + Math.round(value * 100);
});

// Timer
NetworkTables.addKeyListener('/robot/time', (key, value) => {
    // We assume here that value is an integer representing the number of seconds left.
    var min = Math.floor(value / 60);
    var sec = value % 60;
    ui.timer.innerHTML = min + ":" + sec;
});

// Elevator diagram
NetworkTables.addKeyListener('/SmartDashboard/Lift position', (key, value) => {
    var armAngle = value * 3 / 20 - 45;
    // Rotate the arm in diagram to match real arm
    ui.robotDiagram.arm.style.transform = `rotate(${armAngle}deg)`;
});

// Load list of prewritten autonomous modes
NetworkTables.addKeyListener('/SmartDashboard/Auto mode/options', (key, value) => {
    // Clear previous list
    while (ui.autoSelect.firstChild) {
        ui.autoSelect.removeChild(ui.autoSelect.firstChild);
    }

    // Make an option for each autonomous mode and put it in the selector
    for (let i = 0; i < value.length; i++) {
        var option = document.createElement('option');
        option.appendChild(document.createTextNode(value[i]));
        ui.autoSelect.appendChild(option);
    }
    
    // Set value to the already-selected mode. If there is none, nothing will happen.
    ui.autoSelect.value = NetworkTables.getValue('/SmartDashboard/currentlySelectedMode');
});

// Load list of prewritten autonomous modes
NetworkTables.addKeyListener('/SmartDashboard/Auto mode/options', (key, value) => {
    ui.autoSelect.value = value;
});

/**
 * Global Listener that runs whenever any value changes
 * @param {string} key
 * @param value
 * @param {boolean} isNew
 */
function onValueChanged(key, value, isNew) {
    // Sometimes, NetworkTables will pass booleans as strings. This corrects for that.
    if (value === 'true') {
        value = true;
    }
    else if (value === 'false') {
        value = false;
    }
    // The following code manages tuning section of the interface.
    // This section displays a list of all NetworkTables variables (that start with /SmartDashboard/) and allows you to directly manipulate them.
    var propName = key.substring(16, key.length);
    // Check if value is new and doesn't have a spot on the list yet
    if (isNew && !document.getElementsByName(propName)[0]) {
        // Make sure name starts with /SmartDashboard/. Properties that don't are technical and don't need to be shown on the list.
        if (/^\/SmartDashboard\//.test(key)) {
            // Make a new div for this value
            var div = document.createElement('div'); // Make div
            ui.tuning.list.appendChild(div); // Add the div to the page
            var p = document.createElement('p'); // Make a <p> to display the name of the property
            p.appendChild(document.createTextNode(propName)); // Make content of <p> have the name of the NetworkTables value
            div.appendChild(p); // Put <p> in div
            var input = document.createElement('input'); // Create input
            input.name = propName; // Make its name property be the name of the NetworkTables value
            input.value = value; // Set
            // The following statement figures out which data type the variable is.
            // If it's a boolean, it will make the input be a checkbox. If it's a number,
            // it will make it a number chooser with up and down arrows in the box. Otherwise, it will make it a textbox.
            if (typeof value === 'boolean') {
                input.type = 'checkbox';
                input.checked = value; // value property doesn't work on checkboxes, we'll need to use the checked property instead
                input.onchange = function() {
                    // For booleans, send bool of whether or not checkbox is checked
                    NetworkTables.putValue(key, this.checked);
                };
            }
            else if (!isNaN(value)) {
                input.type = 'number';
                input.onchange = function() {
                    // For number values, send value of input as an int.
                    NetworkTables.putValue(key, parseInt(this.value));
                };
            }
            else {
                input.type = 'text';
                input.onchange = function() {
                    // For normal text values, just send the value.
                    NetworkTables.putValue(key, this.value);
                };
            }
            // Put the input into the div.
            div.appendChild(input);
        }
    }
    else {
        // Find already-existing input for changing this variable
        var oldInput = document.getElementsByName(propName)[0];
        if (oldInput) {
            if (oldInput.type === 'checkbox') oldInput.checked = value;
            else oldInput.value = value;
        }
        else console.log('Error: Non-new variable ' + key + ' not present in tuning list!');
    }
}

// THE REST IS JUST EXTRA LISTENERS FOR VARIOUS FEATURES

// Reset gyro value to 0 on click
ui.gyro.container.onclick = function() {
    // Store previous gyro val, will now be subtracted from val for callibration
    ui.gyro.offset = ui.gyro.val;
    // Trigger the gyro to recalculate value.
    updateGyro('/SmartDashboard/drive/navx/yaw', ui.gyro.val);
};

// Reset camera feed
ui.camera.onclick = function() {
    ui.camera.src = "http://10.9.67.2:1181/?action=stream";
};

// Update NetworkTables when autonomous selector is changed
ui.autoSelect.onchange = function() {
    NetworkTables.putValue('/SmartDashboard/autonomous/selected', this.value);
};
