# FakeStandby

## Where is the problem?

Most smartphones have long-living batteries these days, but who doesn't want to keep their phone alive for just one hour more?
While using an app, you maybe not nessesarily need you display. So, why don't turn it off? Up to 20% of your battery power
is consumed by the display.

## Here is the solution!

FakeStandby is an Android app for turning the screen off while keeping apps running. This includes foregroud jobs, which means, you can
keep

- staing online on WhatsApp and other text messengers
- listen to YouTube Videos
- run your prefered game

all with your screen turned off.

## How does it work?

The app is basiccaly launching an overlay over the app currently running. Therefor it is using Android's accessibility services. All it's rendering
on the overlay is a black canvas. effectily turning your pixels of. The apps you're using won't notice this, beacause technically your phone is turned on.

## Disclaimer

Some smartphones have LCD displays with backgroud lighting, that only turns off, when you really press the power button to lock your phone.
The concept works better for OLED or AMOLED displays, where "turning pixels black" is identical to "turning them off"
You should not except huge power saving effects 
