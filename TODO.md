* Document how to build this source, please

* Depend on reprepro and generate index files using standard tools.

* Address the following warnings and errors.

- Nexus should manage its own repository signing keys, and therefore
  there should be no situation where a Release file is not signed:

  W: The repository 'http://nexus.fd.io/content/repositories/fd.io.debian.sid.main ./ Release' is not signed.

* As many of the header fields of the Release file as possible should
  be populated.  Correctly

  W: Invalid 'Date' entry in Release file /var/lib/apt/lists/nexus.fd.io_content_repositories_fd.io.debian.sid.main_._Release

* The sha* sums should be generated correctly:
  Err:1 https://nexus.fd.io/content/repositories/fd.io.debian.sid.main ./ vpp-lib 1.0.0-348~gc843ddc-dirty  Hash Sum mismatch
* Err:2 http://nexus.fd.io/content/repositories/fd.io.debian.sid.main ./ vpp 1.0.0-348~gc843ddc-dirty  Hash Sum mismatch
* Err:2 https://nexus.fd.io/content/repositories/fd.io.debian.sid.main ./ vpp 1.0.0-348~gc843ddc-dirty  Hash Sum mismatch
* E: Failed to fetch https://nexus.fd.io/content/repositories/fd.io.debian.sid.main/./io/fd/vpp/vpp-lib/0.9.5/vpp-lib-0.9.5.deb  Hash Sum mismatch
* E: Failed to fetch https://nexus.fd.io/content/repositories/fd.io.debian.sid.main/./io/fd/vpp/vpp/0.9.5/vpp-0.9.5.deb  Hash Sum mismatch

* File layout in root of repository must match (much of) this snapshot
  from http://ftp.us.debian.org/debian/:

```
Index of /debian

[ICO]         Name            Last modified   Size Description
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[DIR] Parent Directory                           -  
[ ]   README                02-Apr-2016 07:17 1.0K  
[ ]   README.CD-manufacture 26-Jun-2010 05:52 1.3K  
[TXT] README.html           02-Apr-2016 07:17 2.5K  
[TXT] README.mirrors.html   28-Apr-2016 21:52 178K  
[TXT] README.mirrors.txt    28-Apr-2016 21:52  95K  
[DIR] dists/                02-Apr-2016 07:18    -  
[DIR] doc/                  09-May-2016 15:52    -  
[ ]   extrafiles            09-May-2016 17:34  22K  
[DIR] indices/              27-Jul-2015 04:09    -  
[ ]   ls-lR.gz              09-May-2016 17:25  11M  
[DIR] pool/                 19-Dec-2000 15:10    -  
[DIR] project/              17-Nov-2008 18:05    -  
[DIR] tools/                10-Oct-2012 12:29    -  
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Debian Archive

See http://www.debian.org/ for information about Debian GNU/Linux.
```

* The filenames and paths do not comply with DFSG standards and
therefore cannot be indexed with the standard distribution.  Please
change the output filename to meet the standard naming convention (-cjac).

Here are some examples of DFSG compliant .deb filenames:
```
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
```
