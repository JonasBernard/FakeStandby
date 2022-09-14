---
layout: main_page
---


# Welcome to the project website of FakeStandby

![GitHub](https://img.shields.io/github/license/JonasBernard/FakeStandby?color=light-green)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/JonasBernard/FakeStandby)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/JonasBernard/FakeStandby/Android%20CI)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/4235/badge)](https://bestpractices.coreinfrastructure.org/projects/4235)
![GitHub issues](https://img.shields.io/github/issues-raw/JonasBernard/FakeStandby)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/JonasBernard/FakeStandby)
<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
  <input type="hidden" name="cmd" value="_s-xclick" />
  <input type="hidden" name="hosted_button_id" value="SFPCZ53XCNE2A" />
  <input type="image" src="https://img.shields.io/badge/donate_via-paypal-yellow" border="0" name="submit" title="PayPal - The safer, easier way to pay online!" alt="Donate with PayPal button" />
  <img alt="" border="0" src="https://www.paypal.com/en_DE/i/scr/pixel.gif" width="1" height="1" />
</form>

<div class="alert">
  <h2><a href="#disclaimer">Please read the disclaimer.</a></h2>
</div>

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=JonasBernard/FakeStandby&type=Date)](https://star-history.com/#JonasBernard/FakeStandby&Date)

## Where is the problem?

Most smartphones have long-living batteries these days, but who doesn't want to keep their phone alive for just one hour more?
While using an app, you maybe not necessarily need you display. So, why don't turn it off? Up to 20% of your battery power
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
on the overlay is a black canvas, effectively turning your pixels off. The apps you're using won't notice this, because technically your phone is turned on.

## Disclaimer

The application is very programmed in a very "technical" fashion. It may or may not work on your current phone model and with
your particular version of Android. If you face any issues, you could write me or open an issue on GitHub.
However, I can not guarantee to have a proper solution.
I am testing the app on my personal device and as I am a single person developing this application I cannot effort
all device models with every version of Android out there.

Some smartphones have LCD displays with background lighting, that only turns off, when you really press the power button to lock your phone.
The concept works better for OLED or AMOLED displays, where "turning pixels black" is identical to "turning pixels off"
You should not except huge power saving effects on any device especially on devices with LCD display.

## Contribute

Feel free to contribute to the FakeStandby project! Make sure to look at the [Code Of Conduct](conduct) and the [Contributing Information](contribute).

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

You can read the whole license [here](license-text)

## About me

I'm a student from Germany working on this project in
my spare time. Read about me [here](https://jonasbernard.de).
