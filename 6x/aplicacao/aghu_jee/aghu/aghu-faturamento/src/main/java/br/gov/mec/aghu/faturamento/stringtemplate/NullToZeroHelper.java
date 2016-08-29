package br.gov.mec.aghu.faturamento.stringtemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioGrauInstru;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioInEquipe;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioModIntern;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioSaidaUtineo;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStGestrisco;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStMudaproc;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTipoDocPac;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTpContracep;

@SuppressWarnings("ucd")
public class NullToZeroHelper {

	private static NullToZeroHelper instance = new NullToZeroHelper();
	@SuppressWarnings("rawtypes")
	private Map<Class, Object> typeToZeroedObj = null;

	@SuppressWarnings({"unchecked", "PMD.UnusedPrivateMethod"})
	private <E> E getNullZeroedValue(
			final E value,
			@SuppressWarnings("rawtypes") final Class clazz,
			final boolean nullToZero) {

		E result = null;

		result = value;
		if (nullToZero && (result == null)) {
			result = (E) this.typeToZeroedObj.get(clazz);
		}

		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private void init() {

		this.typeToZeroedObj = new HashMap<Class, Object>();
		// boolean
		this.typeToZeroedObj.put(Boolean.class, Boolean.FALSE);
		// numeric
		this.typeToZeroedObj.put(Byte.class, Byte.valueOf((byte) 0));
		this.typeToZeroedObj.put(Short.class, Short.valueOf((short) 0));
		this.typeToZeroedObj.put(Integer.class, Integer.valueOf(0));
		this.typeToZeroedObj.put(Long.class, Long.valueOf(0));
		this.typeToZeroedObj.put(BigInteger.class, BigInteger.ZERO);
		this.typeToZeroedObj.put(Float.class, Float.valueOf(0.0f));
		this.typeToZeroedObj.put(Double.class, Double.valueOf(0.0d));
		this.typeToZeroedObj.put(BigDecimal.class, BigDecimal.ZERO);
		// string
		this.typeToZeroedObj.put(Character.class, Character.valueOf('\0'));
		this.typeToZeroedObj.put(String.class, String.valueOf(""));
		// date
		this.typeToZeroedObj.put(Date.class, new Date(0l));
		// dominio
		this.typeToZeroedObj.put(DominioCpfCnsCnpjCnes.class, DominioCpfCnsCnpjCnes.NAO_APLICAVEL);
		this.typeToZeroedObj.put(DominioInEquipe.class, DominioInEquipe.NAO_APLICAVEL);
		this.typeToZeroedObj.put(DominioModIntern.class, DominioModIntern.NAO_APLICAVEL);
		this.typeToZeroedObj.put(DominioTipoDocPac.class, DominioTipoDocPac.IGNORADO);
		this.typeToZeroedObj.put(DominioTpContracep.class, DominioTpContracep.NENHUM);
		this.typeToZeroedObj.put(DominioGrauInstru.class, DominioGrauInstru.NAO_APLICAVEL);
		this.typeToZeroedObj.put(DominioSaidaUtineo.class, DominioSaidaUtineo.NAO_APLICAVEL);
		this.typeToZeroedObj.put(DominioStMudaproc.class, DominioStMudaproc.NAO);
		this.typeToZeroedObj.put(DominioStGestrisco.class, DominioStGestrisco.NAO);
	}

	private NullToZeroHelper() {

		super();

		this.init();
	}

	public Boolean getNullZeroedValue(final Boolean value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Boolean.class, nullToZero);
	}

	public Byte getNullZeroedValue(final Byte value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Byte.class, nullToZero);
	}

	public Short getNullZeroedValue(final Short value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Short.class, nullToZero);
	}

	public Integer getNullZeroedValue(final Integer value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Integer.class, nullToZero);
	}

	public Long getNullZeroedValue(final Long value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Long.class, nullToZero);
	}

	public BigInteger getNullZeroedValue(final BigInteger value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, BigInteger.class, nullToZero);
	}

	public Float getNullZeroedValue(final Float value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Float.class, nullToZero);
	}

	public Double getNullZeroedValue(final Double value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Double.class, nullToZero);
	}

	public BigDecimal getNullZeroedValue(final BigDecimal value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, BigDecimal.class, nullToZero);
	}

	public Character getNullZeroedValue(final Character value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Character.class, nullToZero);
	}

	public String getNullZeroedValue(final String value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, String.class, nullToZero);
	}

	public Date getNullZeroedValue(final Date value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, Date.class, nullToZero);
	}

	public DominioCpfCnsCnpjCnes getNullZeroedValue(final DominioCpfCnsCnpjCnes value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioCpfCnsCnpjCnes.class, nullToZero);
	}

	public DominioInEquipe getNullZeroedValue(final DominioInEquipe value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioInEquipe.class, nullToZero);
	}

	public DominioModIntern getNullZeroedValue(final DominioModIntern value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioModIntern.class, nullToZero);
	}

	public DominioTipoDocPac getNullZeroedValue(final DominioTipoDocPac value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioTipoDocPac.class, nullToZero);
	}

	public DominioTpContracep getNullZeroedValue(final DominioTpContracep value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioTpContracep.class, nullToZero);
	}

	public DominioGrauInstru getNullZeroedValue(final DominioGrauInstru value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioGrauInstru.class, nullToZero);
	}

	public DominioSaidaUtineo getNullZeroedValue(final DominioSaidaUtineo value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioSaidaUtineo.class, nullToZero);
	}

	public DominioStMudaproc getNullZeroedValue(final DominioStMudaproc value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioStMudaproc.class, nullToZero);
	}

	public DominioStGestrisco getNullZeroedValue(final DominioStGestrisco value, final boolean nullToZero) {

		return this.getNullZeroedValue(value, DominioStGestrisco.class, nullToZero);
	}

	public static NullToZeroHelper getInstance() {

		return instance;
	}
}
