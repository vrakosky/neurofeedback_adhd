const os = require('os');
const Pusher = require('pusher');

// Set up Pusher
const pusher = new Pusher({
  appId: '542049',
  key: '0be1557f329eb466398f',
  secret: '9fe1a11bdc3cf925c556',
  cluster: 'mt1',
  encrypted: true,
});

// To convert from bytes to gigabytes
const bytesToGigaBytes = 1024 * 1024 * 1024;
// To specify the interval (in milliseconds)
const intervalInMs = 100;
//Radian
var rad = 0;
//Random function
function getRandomInt(max) {
  return Math.floor(Math.random() * Math.floor(max));
}

//Initialization file system
var fs = require('fs');


setInterval(() => {
	
// Generating data
var cos = 8*Math.cos(rad/Math.PI);
var sin = 8*Math.sin(rad/Math.PI);

//Push data to terminal
const data1 = cos;
const data2 = sin;

rad++;
console.log(`cos: ${data1}`);
console.log(`sin: ${data2}`);

if (!(isNaN(data1) || isNaN(data2))){
  // To publish to the channel 'stats' the event 'new_memory_stat' 
  pusher.trigger('stats', 'new_memory_stat', {
    data1, data2,
  });}
}, intervalInMs);