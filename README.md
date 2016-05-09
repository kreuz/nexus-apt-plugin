Nexus APT Plugin
================

this plugin generates a Packages.gz for each nexus repository and
allows the repository to be listed in a debian /etc/apt/sources.list
file so that it can be used by aptitude/apt-get/ubuntu software
center.

Installation
============

The 'Downloads' section of this project contains the latest
builds. Please download the latest nexus-apt-plugin-N.N-bundle.zip and
unzip it into the sonatype-work/nexus/plugin-repository/ and restart
nexus.

> to be sure that the index is regenerated (the plugin adds attributes
> to index) it could be neccessary to delete the index files under
> sonatype-work/nexus/indexer

All repositories now contain a Packages.gz that lists all debian
packages the indexer was able to find.

Compatibility
-------------

| Nexus Version      | Plugin Version |
| ------------------ | -------------- |
| 2.11.x and greater | 1.1.2          |
| 2.8.x and greater  | 1.0.2          |
| 2.7.x              | 0.6            |

The plugins might be compatible with earlier Nexus versions, but are
not tested.


Debian Packages from a Maven Build Process
------------------------------------------
https://github.com/sannies/blogger-java-deb is a small example on how
to create debs in a Maven process and works together well with this
plugin.


Pitfall
-------

The indexer cannot find packages when there is a main artifact with
the same name:

If the artifacts are named like:

-  nexus-apt-plugin-0.5.jar
-  nexus-apt-plugin_0.5.deb

The indexer won't index the debian package. In order to make the
indexer index the debian package it needs a name, a version and an
architecture, separated by underscores.

-  nexus-apt-plugin-0.5.jar
-  nexus-apt-plugin_0.5_all.deb

This is fine.

Known Bug
---------

When maven uploads an artifact to Nexus, it uploads 2 files: the deb
file and the pom. Nexus then indexes the artifact, but it does this
for the 2 files simultaneously.  Depending on which file is uploaded
last (seems to be random), the debian package information may or may
not be in the index and therefore may or may not be in the
Packages/Packages.gz files.

TODO:

The filenames and paths do not comply with DFSG standards and
therefore cannot be indexed with the standard distribution.  Please
change the output filename to meet the standard naming convention (-cjac).


Here are some examples of DFSG compliant .deb filenames:

pinentry-curses_0.9.7-5_amd64.deb
policycoreutils_2.5-1_amd64.deb
policycoreutils-python-utils_2.5-1_amd64.deb
pwgen_2.07-1.1_amd64.deb
python3-selinux_2.5-1_amd64.deb
python-audit_1%3a2.4.5-1+b1_amd64.deb
python-ipy_1%3a0.83-1_all.deb
python-selinux_2.5-1_amd64.deb
python-semanage_2.5-1_amd64.deb
python-sepolgen_1.2.3-1_all.deb
python-sepolicy_2.5-1_amd64.deb
python-setools_3.3.8+20151215-3_amd64.deb
rake_10.5.0-2_all.deb
ruby_1%3a2.3.0+4_amd64.deb
ruby2.3_2.3.1-1_amd64.deb
ruby-selinux_2.5-1_amd64.deb
selinux-basics_0.5.4_all.deb
setools_3.3.8+20151215-3_amd64.deb
shorewall_5.0.7.2-1_all.deb
shorewall6_5.0.7.2-1_all.deb
shorewall-core_5.0.7.2-1_all.deb
sshfs_2.5-1_amd64.deb


To fix the index contents, start an 'Update Index' action on the
repository. ( This does not work for me.  I must re-start the entire
nexus service for indexes to be re-written.  Hard on production
environments. -cjac )

This is an issue with the indexer in Nexus 2.x.y. Sonatype is planning
to use a new indexer technology in their Nexus 3.0 release.

Adding a repository to sources.list
===================================

just add the line `deb http://repository.yourcompany.com/content/repositories/releases/ ./`

TODO (cjac): this needs to be changed to

  deb http://${NEXUSPROXY}/.../debian main contrib non-free

to your `/etc/apt/sources.list`. Type `apt-get update` and all debian
packages in the repository can now be installed via `apt-get install`.

Author
======

This plugin was created by https://github.com/sannies and is now
maintained by https://github.com/inventage.
