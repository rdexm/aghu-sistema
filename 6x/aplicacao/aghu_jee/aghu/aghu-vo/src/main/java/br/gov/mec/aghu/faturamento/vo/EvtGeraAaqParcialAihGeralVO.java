package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigInteger;

import br.gov.mec.aghu.model.FatEspelhoAih;

public class EvtGeraAaqParcialAihGeralVO
		implements
			Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3606367478655107454L;

	private final FatEspelhoAih eai;
	private final Short tahSeq;
	private final String orgLocRec;
	private final BigInteger cnes;
	private final Long cgcHosp;
	private final Integer codMunicipio;
	private final Long cpfDirClinico;
	private final BigInteger numeroCNSPaciente;

	public EvtGeraAaqParcialAihGeralVO(
			final FatEspelhoAih eai,
			final Short tahSeq,
			final String orgLocRec, 
			final BigInteger cnes,
			final String cgcHosp, 
			final Integer codMunicipio,
			final String cpfDirClinico, 
			final BigInteger numeroCNSPaciente) {

		super();

		if (eai == null) {
			throw new IllegalArgumentException("Parametro eai nao informado!!!");
		}
		if (eai.getTahSeq() == null) {
			throw new IllegalArgumentException(
					"Nao ha um numero de AIH associado ao espelho");
		}
		if (orgLocRec == null) {
			throw new IllegalArgumentException("Parametro orgLocRec nao informado!!!");
		}
		if (orgLocRec.isEmpty()) {
			throw new IllegalArgumentException("Parametro orgLocRec nao informado!!!");
		}
		if (cnes == null) {
			throw new IllegalArgumentException("Parametro cnes nao informado!!!");
		}
		if (cnes.compareTo(BigInteger.ZERO) < 0) {
			throw new IllegalArgumentException();
		}
		if (cgcHosp == null) {
			throw new IllegalArgumentException("Parametro cgcHosp nao informado!!!");
		}
		if (cgcHosp.isEmpty()) {
			throw new IllegalArgumentException("Parametro cgcHosp nao informado!!!");
		}
		if (codMunicipio == null) {
			throw new IllegalArgumentException("Parametro codMunicipio nao informado!!!");
		}
		if (cpfDirClinico == null) {
			throw new IllegalArgumentException("Parametro cpfDirClinico nao informado!!!");
		}
		//		if (numeroCNSPaciente == null) {
		//			throw new IllegalArgumentException("Parametro numeroCNSPaciente nao informado!!!");
		//		}
		if (tahSeq == null) {
			throw new IllegalArgumentException("Parametro tahSeq nao informado!!!");
		}
		this.eai = eai;
		this.tahSeq = tahSeq;
		this.orgLocRec = orgLocRec;
		this.cnes = cnes;
		this.cgcHosp = Long.valueOf(cgcHosp);
		this.codMunicipio = codMunicipio;
		this.cpfDirClinico = Long.valueOf(cpfDirClinico);
		this.numeroCNSPaciente = numeroCNSPaciente;
	}

	public FatEspelhoAih getEai() {

		return this.eai;
	}

	public String getOrgLocRec() {

		return this.orgLocRec;
	}
	
	public Short getTahSeq() {
	
		return this.tahSeq;
	}

	public BigInteger getCnes() {

		return this.cnes;
	}

	public Long getCgcHosp() {

		return this.cgcHosp;
	}
	
	public Integer getCodMunicipio() {
	
		return this.codMunicipio;
	}

	public Long getCpfDirClinico() {

		return this.cpfDirClinico;
	}

	public BigInteger getNumeroCNSPaciente() {

		return this.numeroCNSPaciente;
	}

}
