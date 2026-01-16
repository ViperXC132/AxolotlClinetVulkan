package io.github.axolotlclient.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.time.MonthDay;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

public class AltIcons {
	/**
	 * <pre>
	 * ::::...';::::::::llodxxxxxxxxxxxxxxxxxxdoll::::::::;,......,
	 * :::,......,;:loxxxxxxxxxxdoooooooodxxxxxxxxxxoc,'...,ok0Kx..
	 * :::,........oxxxxxxxkoc:::::::::::::::ldxxxxdc'...lOXNNNNK..
	 * :::,.......coodxxxxxkxddddollc::lolc:cldxxo;....oKNNNNNNNK..
	 * ::::.......'looodxxxxxxxxxxxxxxdokxxkkxxl'.....kNNNNNNNNN0..
	 * :::;.........,:looodxxkxxxxxxxxxxxxxxxc.......dNNNNNNNNNNx..
	 * ::::,..........;ldxxxxxxxxxxxxxxxxxxx;.......cXNNNNNNNNNX'.'
	 * ::::;..........;cllodddddddddxxdddddc.......c0OKNNNNNNNXo..,
	 * :::::'......:oddddddddddddddddddddddddooooddddONNNNNNNXo...:
	 * ::::::...'cddddddddddddddddddddddddddddddddddxXNNNXKNXl...,:
	 * ::::::;.cddddocccloddddddddddddddddddddddddddxkkkxddXc....::
	 * ::::::codddl'oKNNk.,odddddddddddddddddddddddddddddddd....,::
	 * :::::cddddl.kMMMMN...odddddddddddddl:;;:lddddddddddd;...co::
	 * ::::lddddd:.:KNKx.;'.cdddddddddddl'kNMMN,.cdddddddddc..cddc:
	 * :::lddddddo....lkXM;.oddddddddddl.kMMMMW,..cdddddddddoodddo:
	 * ::cddddddddo:..kKOc.:dddddddddddc.'k0Oo.oo.:ddddddddddddddd:
	 * :cddddddddddddol,.'lddddddddddddd,...x0WMO'odddddddddddddddl
	 * :oddddddddddddo.'ldollloddddddddddl;.oOxlcdddddddddddddddddl
	 * lddddddddddddd,'ddo......'lddddddddddddddddddddddddddddddddl
	 * oddddddddddddd;.oddc.....:odddddddddddddddddddddddddddddddd:
	 * odddddddddddddocddddo::lddddddddddddddddddddddddddddddddddo:
	 * ddddddddddddd;.ldddddlodddddddddddddddddddddddddddddddddddc:
	 * dddddddddddddo.;dddl'.'oddddddc:dddddddddddddddddddddddddd::
	 * ddddddddddddddc.:l'.,l,.ldddl'.:dddddddddddddddddddddddddd::
	 * ddddddddddddddd;..;lddo'.:c'.;odddddddddddddddddddddddddddl:
	 * ddddddddddddddddoddddddd:.':odddddddddddddddddddddddddddddd:
	 * ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd:
	 * ldddddddddddddddddddddddddddddddddddddddddddddddddddddddddc:
	 * ::odddddddddddddddddddddddddddddddddddddddddddddddddddddl:::
	 * ::::clddddddddddddddddddddddddddddddddddddddddddddddolc:::::
	 * ::::::::loodddddddddddddddddddddddddddddddddddddollc::::::::
	 * ::::::::::::ccllooodddddddddddddddddddooolllcc::::::::::::::
	 * </pre>
	 */
	public static Optional<InputStream> getAltIcon() {
		var current = MonthDay.from(ZonedDateTime.now());
		if (current.getMonth().equals(Month.JUNE)) {
			return Optional.of(read("@FOX_PRIDE@"));
		}
		var tvd = MonthDay.of(Month.MARCH, 31);
		var tdor = MonthDay.of(Month.NOVEMBER, 20);
		if (current.equals(tvd) || current.equals(tdor)) {
			return Optional.of(read("@FOX_TRANS@"));
		}
		return Optional.empty();
	}

	private static InputStream read(String s) {
		return Base64.getDecoder().wrap(new ByteArrayInputStream(s.getBytes(StandardCharsets.ISO_8859_1)));
	}
}
