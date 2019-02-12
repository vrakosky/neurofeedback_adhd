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

setInterval(() => {
  const totalMemGb = os.totalmem()/bytesToGigaBytes;
  const freeMemGb = os.freemem()/bytesToGigaBytes;
  const usedMemGb = totalMemGb - freeMemGb;

  const data1 = totalMemGb;
  const data2 = freeMemGb;
  const data3 = usedMemGb;

  console.log(`Total: ${data1}`);
  console.log(`Free: ${data2}`);
  console.log(`Used: ${data3}`);

  // To publish to the channel 'stats' the event 'new_memory_stat' 
  pusher.trigger('stats', 'new_memory_stat', {
    data1, data2, data3,
  });
}, intervalInMs);