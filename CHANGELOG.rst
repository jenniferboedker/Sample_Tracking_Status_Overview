==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.


0.5.0 (2021-09-27)
------------------

**Added**

**Fixed**

* Color status counts based on the number of samples that have passed it (`#69 <https://github.com/qbicsoftware/sample-tracking-status-overview/issues/69>`_)

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
