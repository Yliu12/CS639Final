/**
 * Ying Liu
 * Log data from serial
 *
 * Logger by winston
 * https://github.com/winstonjs/winston#multiple-transports-of-the-same-type
 */

//Logger Obj
var SENSOR_Y, SENSOR_G, LOG_BY_SECOND;

//Socket Obj
var Y_DATA = "";
var G_DATA = "";


//1. Init Logger
initLogger();

//2. Listen to port
portsActions();

//3. Timed socket connection
socketConnection();


/**
 *==================================================initLogger==========================================================
 */
function initLogger() {

    var winston = require('winston');
    winston.loggers.add('SENSOR_Y', {
        console: {
            colorize: true,
            label: 'Yellow',
            level: false
        },

        file: {
            filename: '/Users/yliu12/Desktop/ArduinoLogger/SENSOR_Y.log',
            json: true,
            maxsize: '102400000',
            maxFiles: '10000',
            timestamp: false,
        }
    });

    winston.loggers.add('SENSOR_G', {
        console: {
            colorize: true,
            label: 'Green',
            level: false
        },

        file: {
            filename: '/Users/yliu12/Desktop/ArduinoLogger/SENSOR_G.log',
            json: true,
            maxsize: '102400000',
            maxFiles: '10000',
            timestamp: false,
        }
    });
    winston.loggers.add('LOG_BY_SECOND', {
        console: {
            colorize: true,
            label: 'bySecond',
            level: false
        },

        file: {
            filename: '/Users/yliu12/Desktop/ArduinoLogger/LOG_BY_SECOND.log',
            json: false,
            maxsize: '1024000',
            maxFiles: '10000',
            timestamp: true,
        }
    });


    SENSOR_Y = winston.loggers.get('SENSOR_Y');
    SENSOR_G = winston.loggers.get('SENSOR_G');
    LOG_BY_SECOND = winston.loggers.get('LOG_BY_SECOND');
};


/**
 *=======================================================serialPort=======================================================
 */
function portsActions() {

    var serialPort = require("serialport");
    var ArduinoSerial = [];
    serialPort.list(function (err, ports) {
        ports.forEach(function (port) {
            console.log(port);

            console.log('----------------------------------');
            if (port.manufacturer && port.serialNumber.indexOf("553383436393516041C0") != -1) {
                ArduinoSerial.sensorG = port.comName;
            }
            if (port.manufacturer && port.serialNumber.indexOf("85531303431351B060F1") != -1) {
                ArduinoSerial.sensorY = port.comName;
            }
        });
        console.log(ArduinoSerial.toString());

        //StartLogger
        logportsdata(ArduinoSerial);
    });

}


/**
 * ====================================================logportsdata=====================================================
 * logportsdata
 * @param portLists
 */
function logportsdata(portLists) {

    //require serialport
    var SerialPort = require('serialport');

    /*
     Port gor Sensor G
     */
    var portG = new SerialPort(portLists.sensorG, {
        parser: SerialPort.parsers.readline('\r\n'),
        baudRate: 57600

    });
    portG.on('data', function (data) {

        if (data.toString() != "") {

            //Log Sensor Data
            SENSOR_G.info('G: ' + data.toString());

            //Push to Socket String
            if (data.toString() != "0") {
                G_DATA= G_DATA+data.toString();
            }
        }


    });

    // open errors will be emitted as an error event
    portG.on('error', function (err) {
        //SerialPort.parsers.raw;
        console.log(err.toString());
    });


    /*
     Port gor Sensor Y
     */
    var portY = new SerialPort(portLists.sensorY, {
        parser: SerialPort.parsers.readline('\r\n'),
        baudRate: 57600

    });
    portY.on('data', function (data) {
        if (data.toString() != "") {

            //Log Sensor Data
            SENSOR_Y.info('Y: ' + data.toString());

            //Push to Socket String
            if (data.toString() != "0") {
                //console.log('b:' + data);
                Y_DATA=Y_DATA+data.toString();
            }
        }
    });

// open errors will be emitted as an error event
    portY.on('error', function (err) {
        console.log(err.toString());
    });

};


/**
 * =====================================================================================================================
 * Loop every second
 * 1. second Log
 * 2. Socket Connection
 *
 */
function socketConnection() {

    var net = require('net');

    var HOST = '127.0.0.1';
    var PORT = 9999;
    setInterval(function () {
        var client = new net.Socket();
        client.connect(PORT, HOST, function () {

            // Start COnnection and Send data
            var thisSecondData = "\n"+"Y"+Y_DATA+"\n"+"G"+G_DATA;
            client.write(thisSecondData);
            client.write("\n=====================================");

            var thisSecondLog = "N";

            if(Y_DATA && ""!= Y_DATA ){
                thisSecondLog = "Y";
                if(G_DATA && ""!= G_DATA){
                    thisSecondLog = "B";
                }
            }else if(G_DATA && ""!= G_DATA){
                thisSecondLog = "G";
            }

            console.log(thisSecondData);
            LOG_BY_SECOND.info(thisSecondLog);

            Y_DATA = "";
            G_DATA = "";

            client.end();

        });

        //add Action
        client.on('data', function (data) {
            console.log("*****************************************************");

            console.log('Prediction: ' + data);

            console.log("*****************************************************");


            client.destroy();

        });

        // on close
        client.on('close', function () {
            console.log('===========================================Connection closed=============================');

        });

    /*    client.on('error', function (err) {
            console.log(err);
        });
*/
    }, 1000);
}





