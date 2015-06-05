#!/usr/bin/perl -w

use strict;


use lib qw( ..);
use JSON;
use Data::Dumper;

my $data;


{
    local $/ = undef;
    my $json_string = <>;
    #print $json_string;
    my $json = JSON->new;
    $data = $json->decode($json_string);
}

my $bundles = $data->{data};

foreach my $bundle ( @$bundles) {
    my $symbolicname = $bundle->{symbolicName};
    my $version = $bundle->{version};
    
    if ($symbolicname =~ /org\.apache\.sling/) {
        print "$symbolicname,$version\n";
    }
}

