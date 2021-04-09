# Holidays

__Legal holidays, weekends, workdays by date (for Germany and Austria, but extendable).__

For some applications I had use cases where it would be nice to figure out if a given date was a holiday or not. 
'Holiday' is defined here as legal holidays, although the concept of 'holiday' depends on the context. For my use cases
it was mainly days that are somehow similar to Sundays, i.e. officially work free, and similar legal rules apply.

Since I didn't find a library available, just some online services without free access, and this requirement showed 
up in a customer project, I decided to try to develop some library for that myself.

Unluckily, there is some complexity about those holidays:

* In Germany and Austria (my focus on development) there are federal, state and regional holidays, so the region is 
  important.
  
* The rules change, so some holidays start to be holidays in some year and end in another, or are one-time holidays 
  in a given year.
  
* Some holidays are not on the same date every year, but depend on easter - which requires some astronomical computation
  about the moon.

* There are various legal rules about holidays, and holidays are often not treated same - so the set of holidays to take
  into account depends on the application.


The library tries to address those problems:

* The definition of holidays (basically a function year -> date) is separated from the definition of regions, and the
  regions are a set of holidays that are valid in the given region. To simplify the configuration the regions are 
  configured hierarchically, i.e. the region of state "Baden-Württemberg" has the parent region "Germany" and thus
  includes all holidays defined for region "Germany" and just adds the extra state holidays.
  
* The assignment of holiday to region can have a list of range expressions, like "-1920,1925-1950,2017,2020-":
  the holiday was valid before and 1920, from 1925-1950, at 2017 and from 2020. All specified years are inclusive.
  
* Holidays can be defined as "easter-relative", i.e. easter date plus/minus an day offset. And there's also the possibility
  to define a holiday date by a weekday before or after a given date (required for Pencance Day - "Buß- und Bettag",
  which is dated to Wednesday before Nov. 27 every year).
  
* You can define your own holidays and regions, if you have special need for them. 

## Limitiations

The holiday-to-region mapping is state-level for Germany and nation-wide holidays in Austria. So regional holidays in
Germany and state holidays in Austria are not defined in the default configuration.

Holidays in Austria that only apply or have applied to religious groups (Good Friday, Yom Kippur) are not defined for Austria. 

## How to use

The library is build für Java 8+ and has the single dependency slf4j-api.

The default configuration is in the libraries properties files, see 
[holiday.properties](lib/src/main/resources/holiday.properties), [region_DE.properties](lib/src/main/resources/region_DE.properties)
and [region_AT.properties](lib/src/main/resources/region_AT.properties).

An example app for using the default configuration is [HolidaysApp.java](examples/src/main/java/net/fumix/holidays/HolidaysApp.java).

For programmatic configuration have a look at 
[HolidaysProgrammaticConfigApp.java](examples/src/main/java/net/fumix/holidays/HolidaysProgrammaticConfigApp.java).

## Quality

Hopefully good, but for now it's a first version with limited tests.

The accuracy should be ok, but some regional settings are missing and the year-to-region mapping is not going back too
long (back until 1990 should be ok for Germany).

## References

https://de.wikipedia.org/wiki/Gesetzliche_Feiertage_in_Deutschland#cite_note-bu%C3%9F2-27

https://de.wikipedia.org/wiki/Feiertage_in_%C3%96sterreich

## Things I learned

While trying to figure out about to compute Yom Kippur for the Austrian holiday region, I realized that the Hebrew calender
is... complex. I found no code examples to cut-copy-paste for the limited use case of Yom Kippur date in the Gregorian
calendar, but I stumbled upon https://kosherjava.com/zmanim-project/ - a Java library computing start and end times for Jewish
prayers and holidays. I knew that Jewish holidays start and end in the evenings and not on midnight, but I didn't know
how 'evening' is defined:

> Please note: due to atmospheric conditions (pressure, humidity and other conditions), calculating zmanim accurately is 
> very complex. The calculation of zmanim is dependent on Atmospheric refraction (refraction of sunlight through the 
> atmosphere), and zmanim can be off by up to 2 minutes based on atmospheric conditions. Inaccuracy is increased by 
> elevation. It is not the intent of this API to provide any guarantee of accuracy. See Using a Digital Terrain Model 
> to Calculate Visual Sunrise and Sunset Times for additional information on the subject.

I finally decided that I didn't include Yom Kippur, because it's not a general holiday in Austria, but if somebody has
a little code-fragment to map Gregorian dates onto the Jewish calender I would be happy to include it (midnight to midnight, i.e.!)
