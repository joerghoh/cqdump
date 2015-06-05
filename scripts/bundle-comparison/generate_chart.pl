#!/usr/bin/perl -w

use strict;

my $bundles = {};
my @versions;

foreach my $file (@ARGV) {
  my $content;
  open(my $fh, $file) or die "Cannot open $file: $!\n";
  {
    local $/;
    $content = <$fh>;
  }
  close($fh);

  foreach my $line (split ("\n", $content)) {
    (my $name,my $version) = split (",", $line);
    $bundles->{$name}->{$file} = "$version";
  }
  push @versions,$file;

}

my @sorted_versions = sort @versions;;

# print table header
print "<table>\n  <tr>\n";
print "    <th>Symbolic name of the bundle</th>\n";
foreach my $v (@sorted_versions) {
  print "    <th>$v</th>\n";
}
print "  </tr>\n";

#print table rows
foreach my $r (sort keys ($bundles)) {
  print "<tr><td>$r</td>";
  foreach my $v (@sorted_versions) {
    if (exists $bundles->{$r}->{$v}) {
      print "<td>" . $bundles->{$r}->{$v} . "</td>";
    } else {
      print "<td>-</td>";
    }
  }
  print "</tr>\n";
}

print "</table>\n";

