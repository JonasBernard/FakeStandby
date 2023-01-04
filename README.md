<div>
   <p align="center">
     <img src="https://socialify.git.ci/JonasBernard/FakeStandby/image?description=1&font=Bitter&forks=1&issues=1&logo=https%3A%2F%2Fgithub.com%2FJonasBernard%2FFakeStandby%2Fraw%2Fmaster%2Fbranding%2Fapp_icon_round_opaque.svg&owner=1&pattern=Circuit%20Board&stargazers=1&theme=Dark" alt="FakeStandby" width="640" height="320" /><br>
     <a href="https://f-droid.org/packages/android.jonas.fakestandby/">
       <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="60">
     </a><br>
     <a href="https://github.com/JonasBernard/FakeStandby/releases">
       <img src="https://img.shields.io/github/v/release/JonasBernard/FakeStandby" alt="GitHub release (latest by date)">
     </a><br>
     <a href="https://github.com/JonasBernard/FakeStandby/commits/master">
       <img src="https://img.shields.io/github/last-commit/JonasBernard/FakeStandby" alt="GitHub last commit">
     </a>
     <img src="https://img.shields.io/github/repo-size/JonasBernard/FakeStandby?label=repository%20size" alt="GitHub repo size">
     <br>
     <a href="https://drone.jonasbernard.de/JonasBernard/FakeStandby">
       <img src="https://drone.jonasbernard.de/api/badges/JonasBernard/FakeStandby/status.svg" alt="Drone Pipeline Status" />
     </a>
     <a href="https://github.com/JonasBernard/FakeStandby/actions/workflows/android.yml">
       <img src="https://img.shields.io/github/actions/workflow/status/JonasBernard/FakeStandby/android.yml" alt="GitHub Workflow Status">
     </a>
     <a href="https://fakestandby.jonasbernard.de/">
       <img src="https://img.shields.io/website?down_color=red&down_message=offline&up_color=light-green&up_message=online&url=https%3A%2F%2Ffakestandby.jonasbernard.de" alt="Website Status">
     </a><br>
     <a href="https://bestpractices.coreinfrastructure.org/projects/4235">
       <img src="https://bestpractices.coreinfrastructure.org/projects/4235/badge" alt="CII Best Practices">
     </a>
   </p>
</div>

## What is the problem?

Most smartphones these days have long-living batteries, but who doesn't want their phone to last for just an hour more?
While using an app, you maybe not necessarily need your display. Up to 20% of your battery power is consumed by the display.
So, why not turn off all the pixels on it? Plus, this app can help prevent burn-in caused by statically displayed images and text.

## Here is the solution!

FakeStandby is an Android app to turn off your screen while keeping apps running. This includes foreground jobs, which means
you can keep

- listening to podcasts
- listening to music on YouTube
- staying online with various text messengers
- running your favorite game

all with your screen turned off!

## How does it work?

This app displays an overlay over the foreground app using Android's accessibility services. All it renders on the overlay
is a black canvas, effectively turning your pixels off. The apps you're using won't notice this, because technically your phone is turned on.

## Disclaimer

Some smartphones have LCD displays with background lighting that only turns off when you actually press the power button to lock your phone.
The concept of this app works better with OLED or AMOLED displays, where "turning pixels black" is identical to "turning pixels off".
You should not except huge power saving effects on any device, especially on devices with LCD displays.


## Contribute

![Maintenance](https://img.shields.io/maintenance/yes/2022)

Feel free to contribute to the FakeStandby project! Make sure to look at the [Code Of Conduct](CODE_OF_CONDUCT.md) and the [Contributing Information](CONTRIBUTING.md).

## Special Thanks

Special thanks goes to all contributors! You can find them in [CONTRIBUTORS.md](CONTRIBUTORS.md).
I also want to thank all the nice people who have donated some money!

## Licensing

![GitHub](https://img.shields.io/github/license/JonasBernard/FakeStandby?color=light-green)


FakeStandby is an Android app that turns off your screen while keeping apps running.

Copyright (C) 2023  Jonas Bernard

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
