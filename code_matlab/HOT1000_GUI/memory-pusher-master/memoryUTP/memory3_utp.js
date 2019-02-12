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

// To specify the interval (in milliseconds)
const intervalInMs = 170;

//Initialization file system
var fs = require('fs');
var Hbt_left, Hbt_right, heart_rate, Hbt_left_filtered, Hbt_right_filtered = 0;

setInterval(() => {
	
// Reading data
fs.readFile('../data1.txt', 'utf-8' ,function(err, buf) {
  //data = console.log(buf.toString());
  Hbt_left = parseFloat(buf.toString());
});
fs.readFile('../data2.txt', 'utf-8' ,function(err, buf) {
  Hbt_right = parseFloat(buf.toString());
});
fs.readFile('../data3.txt', 'utf-8' ,function(err, buf) {
  heart_rate = parseFloat(buf.toString());
});
fs.readFile('../data1filtered.txt', 'utf-8' ,function(err, buf) {
  Hbt_left_filtered = parseFloat(buf.toString());
});
fs.readFile('../data2filtered.txt', 'utf-8' ,function(err, buf) {
  Hbt_right_filtered = parseFloat(buf.toString());
});


//Push data to terminal
const data1 = Hbt_left;
const data2 = Hbt_right;
const data3 = heart_rate;
const data4 = Hbt_left_filtered;
const data5 = Hbt_right_filtered;

console.log(`Hbt_left: ${data1}`);
console.log(`Hbt_right: ${data2}`);
console.log(`heart_rate: ${data3}`);
console.log(`Hbt_left_filtered: ${data4}`);
console.log(`Hbt_right_filtered: ${data5}`);


if (!(isNaN(data1) || isNaN(data2) || isNaN(data3) || isNaN(data4) || isNaN(data5))){
  // To publish to the channel 'stats' the event 'new_memory_stat' 
  pusher.trigger('stats', 'new_memory_stat', {
    data1, data2, data3, data4, data5,
  });}
}, intervalInMs);
