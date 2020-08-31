# FakeStandby

![GitHub](https://img.shields.io/github/license/JonasBernard/FakeStandby?color=light-green)
![GitHub repo size](https://img.shields.io/github/repo-size/JonasBernard/FakeStandby?label=repository%20size)


![GitHub commit activity](https://img.shields.io/github/commit-activity/w/JonasBernard/FakeStandby)
![GitHub last commit](https://img.shields.io/github/last-commit/JonasBernard/FakeStandby)

![GitHub release (latest by date)](https://img.shields.io/github/v/release/JonasBernard/FakeStandby)

![Android CI](https://github.com/JonasBernard/FakeStandby/workflows/Android%20CI/badge.svg)
![Website](https://img.shields.io/website?down_color=red&down_message=offline&up_color=light-green&up_message=online&url=https%3A%2F%2Fjonasbernard.github.io%2FFakeStandby%2F)

[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/4235/badge)](https://bestpractices.coreinfrastructure.org/projects/4235)

![GitHub issues](https://img.shields.io/github/issues-raw/JonasBernard/FakeStandby)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/JonasBernard/FakeStandby)


## Where is the problem?

Most smartphones these days have long-living batteries, but who doesn't want their phone to last for just an hour more?
While using an app, you maybe not nessesarily need your display. So, why don’t turn it off? Up to 20% of your battery
power is consumed by the display.

## Here is the solution!

FakeStandby is an Android app to turn off your screen while keeping apps running. This includes foreground jobs, which means
you can keep

- listening to music on YouTube
- staying online on WhatsApp and other text messengers
- running your favorite game

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
