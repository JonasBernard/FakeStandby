# !! TEMPORARY UNMAINTAINTED !!
# I AM ON VACATION TILL 11 OCT, 2020 AND WILL NOT GUARANTEE TO RESOND TO ISSUES AND PULL REQUESTS.
Just be kind and wait. I'll read them as soon as possible.


<div>
   <h1 align="center">FakeStandby<br><h6 align="center">An Android app for turning the screen off while keeping apps running.</h6></h1>
   <p align="center">
     <img src="branding/app_icon_round.svg" height="150"><br>
     <a href="https://f-droid.org/packages/android.jonas.fakestandby/">
       <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="60">
     </a><br>
     <a href="https://github.com/JonasBernard/FakeStandby/releases">
       <img src="https://img.shields.io/github/v/release/JonasBernard/FakeStandby" alt="GitHub release (latest by date)">
     </a><br>
     <a href="https://github.com/JonasBernard/FakeStandby/graphs/commit-activity">
       <img src="https://img.shields.io/github/commit-activity/w/JonasBernard/FakeStandby" alt="GitHub commit activity">
     </a>
     <a href="https://github.com/JonasBernard/FakeStandby/commits/master">
       <img src="https://img.shields.io/github/last-commit/JonasBernard/FakeStandby" alt="GitHub last commit">
     </a>
     <img src="https://img.shields.io/github/repo-size/JonasBernard/FakeStandby?label=repository%20size" alt="GitHub repo size">
     <br>
     <img src="https://img.shields.io/github/workflow/status/JonasBernard/FakeStandby/Android%20CI" alt="GitHub Workflow Status">
     <a href="https://jonasbernard.github.io/FakeStandby/">
       <img src="https://img.shields.io/website?down_color=red&down_message=offline&up_color=light-green&up_message=online&url=https%3A%2F%2Fjonasbernard.github.io%2FFakeStandby%2F" alt="Website Status">
     </a><br>
     <a href="https://bestpractices.coreinfrastructure.org/projects/4235">
       <img src="https://bestpractices.coreinfrastructure.org/projects/4235/badge" alt="CII Best Practices">
     </a>
     <a href="https://github.com/JonasBernard/FakeStandby/issues">
       <img src="https://img.shields.io/github/issues-raw/JonasBernard/FakeStandby" alt="GitHub issues">
     </a>
     <a href="https://github.com/JonasBernard/FakeStandby/pulls">
       <img src="https://img.shields.io/github/issues-pr-raw/JonasBernard/FakeStandby" alt="GitHub pull requests">
     </a>
   </p>
</div>

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
![Maintenance](https://img.shields.io/maintenance/yes/2020)

Feel free to contribute to the FakeStandby project! Make sure to look at the [Code Of Conduct](CODE_OF_CONDUCT.md) and the [Contributing Information](CONTRIBUTING.md).

## Licensing 
![GitHub](https://img.shields.io/github/license/JonasBernard/FakeStandby?color=light-green)

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
