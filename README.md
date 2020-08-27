# FakeStandby

![Android CI](https://github.com/JonasBernard/FakeStandby/workflows/Android%20CI/badge.svg)

## Where is the problem?

Most smartphones have long-living batteries these days, but who doesn't want to keep their phone alive for just one hour more?
While using an app, you maybe not nessesarily need you display. So, why don't turn it off? Up to 20% of your battery power
is consumed by the display.

## Here is the solution!

FakeStandby is an Android app for turning the screen off while keeping apps running. This includes foreground jobs, which means you can
keep

- staying online on WhatsApp and other text messengers
- listen to YouTube Videos
- run your preferred game

all with your screen turned off.

## How does it work?

The app is basically launching an overlay over the app currently running. Therefore it is using Android's accessibility services. All it's rendering
on the overlay is a black canvas, effectifly turning your pixels off. The apps you're using won't notice this, beacause technically your phone is turned on.

## Disclaimer

Some smartphones have LCD displays with background lighting, that only turns off, when you really press the power button to lock your phone.
The concept works better for OLED or AMOLED displays, where "turning pixels black" is identical to "turning pixels off"
You should not except huge power saving effects on any device especially on devices with LCD display.

## Contribute

Feel free to contribute to the FakeStandby project! Make sure to look at the [Code Of Conduct](CODE_OF_CONDUCT.md) and the [Contributing Information](CONTRIBUTING.md).

## Licensing

FakeStandby is an android app that turns off your screen while keeping apps running.
Copyright (C) 2020  Jonas Bernard

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
