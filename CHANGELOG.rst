==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.0.0-beta.10-SNAPSHOT (2022-01-07)
-----------------------------------

**Added**

**Fixed**

**Dependencies**

**Deprecated**

1.0.0-beta.9 (2022-01-04)
---------------------------

**Added**

**Fixed**

* CVE-2021-44832

**Dependencies**

* ``org.apache.logging.log4j:log4j-core:2.17.0`` -> ``2.17.1``
* ``org.apache.logging.log4j:log4j-api:2.17.0`` -> ``2.17.1``

**Deprecated**

1.0.0-beta.8 (2021-12-22)
---------------------------

**Added**

* Color sample status (#177)

* Use person table instead of subscriber (#183)

**Fixed**

* Manifest file download is reset correctly after project selection change (`#169 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/169>`_)

* CVE-2021-45105

* CVE-2021-33609

**Dependencies**

* ``org.apache.logging.log4j:log4j-core:2.16.0`` -> ``2.17.0``

* ``org.apache.logging.log4j:log4j-api:2.16.0`` -> ``2.17.0``

* ``com.vaadin:vaadin-bom:8.14.0`` -> ``8.14.1``

1.0.0-beta.7 (2021-12-15)
---------------------------

**Added**

**Fixed**

* CVE-2021-45046

**Dependencies**

* ``org.apache.logging.log4j:log4j-core:2.15.0`` -> ``2.16.0``

* ``org.apache.logging.log4j:log4j-api:2.15.0`` -> ``2.16.0``

1.0.0-beta.6 (2021-12-13)
---------------------------

**Added**

**Fixed**

* CVE-2021-44228

**Dependencies**

* ``org.apache.logging.log4j:log4j-core:2.13.2`` -> ``2.15.0``

* ``org.apache.logging.log4j:log4j-api:2.13.2`` -> ``2.15.0``

**Deprecated**

1.0.0-beta.5 (2021-12-07)
---------------------------

**Added**

* Improve UI (`#157 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/157>`_)

* Add new column with checkboxes to subscribe/unsubscribe directly for each project (`#162 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/162>`_)

**Fixed**

* Address error when double-clicking a project with no samples (`#164 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/164>`_)

**Dependencies**

**Deprecated**

1.0.0-beta.4 (2021-11-22)
---------------------------

**Added**

* Add sample view, showing all samples of a project with some sample details (`#150 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/150>`_)

**Fixed**

**Dependencies**

**Deprecated**

1.0.0-beta.3 (2021-11-08)
---------------------------

**Added**

* Introduce split panel for showing failing QC samples (`#140 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/140>`_)

* Minor refactoring of the page organisation (`#141 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/141>`_)

* Add filtering to the ProjectId and ProjectTitle Columns (`#142 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/142>`_)

**Fixed**

* More informative message after subscription change (`#144 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/141>`_)

**Dependencies**

**Deprecated**

1.0.1-beta (2021-10-28)
---------------------------

**Added**

**Fixed**

**Dependencies**

* com.vaadin 8.13.0 -> 8.14.0 (addresses CVE-2021-37714)

**Deprecated**


1.0.0-beta (2021-10-26)
---------------------------

**Added**

* Information on existing subscriptions is now shown when selecting a project (`#93 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/93>`_)

* Unsubscribe from project (`#129 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/129>`_)

* Samples with failed QC are shown to the user directly after selecting a project (`#138 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/138>`_)

**Fixed**

* Show correct number of passing QC numbers (`#130 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/130>`_)

**Dependencies**

**Deprecated**


0.6.0 (2021-10-11)
------------------

**Added**

* Show passing QC samples instead of failing QC samples (`#121 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/121>`_)

* Removes manual sorting option from project grid

* Projects are now sorted by the last change as found in the sample tracking database  (`#114 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/114>`_)

**Fixed**

* Make grid take up all space of the side (`#112 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/112>`_)

**Dependencies**

**Deprecated**


0.5.0 (2021-09-27)
------------------

**Added**

**Fixed**

* Color status counts based on the number of samples that have passed it (`#69 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/69>`_)

* Show total number of known samples for every status (`#65 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/65>`_)

**Dependencies**

**Deprecated**


0.4.0 (2021-09-13)
------------------

**Added**

* List failing QC samples for single projects (`#77 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/77>`_)

* Count of samples that finished library prep is shown (`#89 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/89>`_)

* Enable user to subscribe to individual projects (`#84 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/84>`_)

**Fixed**

* Remove possibility of duplicate subscription of user per project (`#95 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/95>`_)

**Dependencies**

**Deprecated**


0.3.0 (2021-08-30)
------------------

**Added**

* Count of available sample data is shown (`#58 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/58>`_)

* Count of samples that failed QC is shown (`#51 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/51>`_)

* Download of available samples is possible as a manifest file (`#54 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/54>`_)

**Fixed**

**Dependencies**

**Deprecated**


0.2.1 (2021-07-20)
------------------

**Added**

**Fixed**

* The correct authentication provider is now used.

**Dependencies**

**Deprecated**


0.2.0 (2021-07-20)
------------------

**Added**

* Received samples are now counted (`#41 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/41>`_)

* Use case counting all samples of a project and the subset of samples having been received at the lab  (`#38 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/38>`_)

* Add a logging facade to be used in the business domain. (`#40 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/40>`_)

* Add a database connector for the sample tracking database. (`#39 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/39>`_)

* Shows the number of received sample to each customer (`#36 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/36>`_)

* Add resource service for project sample status counts. (`#34 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/34>`_)

* Interface for counting samples and received samples given a project code

* Add resource service for project samples status list. (`#34 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/34>`_)

**Fixed**

**Dependencies**

* Add ``org.apache.commons:commons-dbcp2:2.7.0``

* Add ``mysql:mysql-connector-java:8.0.25``


**Deprecated**


0.1.0 (2021-07-07)
------------------

**Added**

* Introduce user notifications (`#29 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/29>`_)

* Introduce a grid showing all project codes and titles for a user (`#27 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/27>`_)

* Add functionality to load projects for a given user (`#25 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/25>`_)

* Provide the authentication provider id with the user information

* Add a mechanism for in app communication between components (`#23 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/23>`_)

* Provides a ``life.qbic.portal.sampletracking.system.SystemContext`` class, that provides the current logged in user (`#21 <https://github.com/qbicsoftware/sample-tracking-status-overview/pull/21>`_)

* Github workflow that checks that the changelog has been updated

* Created the project using cookietemple

**Fixed**

**Dependencies**

**Deprecated**
