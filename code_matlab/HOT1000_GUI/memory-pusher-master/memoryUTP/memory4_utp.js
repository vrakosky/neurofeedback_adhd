const os = require('os');
const Pusher = require('pusher');

// Set up Pusher
const pusher = new Pusher({
  appId: '583139',
  key: '6e710bfa15232092ed2d',
  secret: '979f97745de42555d200',
  cluster: 'mt1',
  encrypted: true,
});

// To convert from bytes to gigabytes
const bytesToGigaBytes = 1024 * 1024 * 1024;
// To specify the interval (in milliseconds)
const intervalInMs = 90;
//Radian
var nb = 0;
//Random function
function getRandomInt(max) {
  return Math.floor(Math.random() * Math.floor(max));
}

//Initialization file system
var fs = require('fs');


setInterval(() => {
	
// Writing data
var fx1 = nb + 10*Math.sin(nb);
var fx2 = Math.pow(nb,10);

//Push data to terminal
const data1 = fx1;
const data2 = fx2;
nb++;
console.log(`fx1: ${data1}`);
console.log(`fx2: ${data2}`);

if (!(isNaN(data1) || isNaN(data2))){
  // To publish to the channel 'stats' the event 'new_memory_stat' 
  pusher.trigger('stats', 'new_memory_stat', {
    data1, data2,
  });}
}, intervalInMs);