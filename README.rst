Sample Tracking Status Overview
-----------------------------------

|maven-build| |maven-test| |release| |java| |groovy|


Gives a visual overview of sample statuses

* Free software: MIT
* Documentation: https://Sample-Tracking-Status-Overview.readthedocs.io.

Features
--------

* TODO

Test environment
----------------

You can run the application in a ``testing`` mode:

.. code-block:: bash

  mvn clean jetty:run -Denvironment=testing -Demail=<email> -Dauth_id=<auth provider id>

You also see, that you need to provide two more properties then, ``email`` and ``auth_id``. The email property is used
to form you portal-user id, the auth-id is the one that is provided by the authentication provider after successful login into the portal.
As wen run in test mode here, you need to simulate it with the properties.


Credits
-------

This project was created with qube_.

.. _qube: https://github.com/qbicsoftware/qube

.. |maven-build| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Build%20Maven%20Package/badge.svg
    :target: https://github.com/qbicsoftware/sample-tracking-status-overview/workflows/Build%20Maven%20Package/badge.svg
    :alt: Github Workflow Build Maven Package Status

.. |maven-test| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Run%20Maven%20Tests/badge.svg
    :target: https://github.com/qbicsoftware/sample-tracking-status-overview/workflows/Run%20Maven%20Tests/badge.svg
    :alt: Github Workflow Tests Status

.. |release| image:: https://img.shields.io/github/v/release/qbicsoftware/offer-manager-2-portlet.svg
    :target: https://github.com/qbicsoftware/sample-tracking-status-overview/release
    :alt: Release status

.. |java| image:: https://img.shields.io/badge/language-java-blue.svg
    :alt: Written in Java

.. |groovy| image:: https://img.shields.io/badge/language-groovy-blue.svg
    :alt: Written in Groovy
