const http = require('http');

// :: String -> (Error -> JSON -> ()) -> ()
const get = (url, callback) => {
  http.get(url, (resp) => {
    let data = '';

    resp.on('data', (chunk) => {
      data += chunk;
    });

    resp.on('end', () => {
      return callback(null, JSON.parse(data));
    });

  }).on("error", callback);
};

let i = 0;
let startTime = Date.now();

const URL = 'http://localhost:8080';

// :: () -> ()
const callCityEndpoint = () => {
  if (i >= 50) return;
  i++;
  return get(URL + '/city/Bangkok', (err, res) => {
    if (err) throw err;
    console.log(`Elapsed: ${Date.now() - startTime}ms`);
    if (Array.isArray(res)) {
      console.log(`${i}th call went through`);
    } else {
      console.log(`${i}th call was rejected`);
    }
    console.log('======================');
    return setTimeout(callCityEndpoint, 400);
  });
};

callCityEndpoint();
