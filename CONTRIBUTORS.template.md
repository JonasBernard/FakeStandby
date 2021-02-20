# Contributors

> I want to thank all contributors that help to develop this app!

## Code contributors

> Code contributions are the most powerful change at a projects heart.
> Those require very precise and good work. Thanks to all code contributors!

{% for c in githubContributors %} 
* [{{ c.login }}]({{ c.html_url }}) ({% if c.contributions > 1 %}{{ c.contributions }} contributions{% else%}1 contribution{% endif%}) {% endfor %}

## POEditor (Translation)

> Special thanks also goes to the great people helping with the translations!
> The app is now already translated into {{ languages.length }} languages!

{% for c in poeditorContributors %} 
* {{ c.name }} {% endfor %}