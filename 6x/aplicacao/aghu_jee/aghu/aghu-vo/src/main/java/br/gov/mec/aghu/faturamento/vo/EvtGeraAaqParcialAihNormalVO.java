package br.gov.mec.aghu.faturamento.vo;

import java.math.BigInteger;
import java.util.List;

import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatEspelhoAih;

public class EvtGeraAaqParcialAihNormalVO
		extends
			EvtGeraAaqParcialAihGeralVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3606367478655107454L;

	private final List<FatAtoMedicoAih> listaAma;
	private final Short modalidadeIntern;

	public EvtGeraAaqParcialAihNormalVO(
			final FatEspelhoAih eai,
			final Short tahSeq,
			final String orgLocRec, 
			final BigInteger cnes,
			final String cgcHosp, 
			final Integer codMunicipio,
			final String cpfDirClinico,
			final List<FatAtoMedicoAih> listaAma,
			final Short modalidadeIntern, 
			final BigInteger numeroCNSPaciente) {

		super(
				eai, tahSeq, orgLocRec, cnes, cgcHosp, codMunicipio, cpfDirClinico, numeroCNSPaciente);

		if (modalidadeIntern == null) {
			throw new IllegalArgumentException("Parametro modalidadeIntern nao informado!!!");
		}
		this.listaAma = listaAma;
		this.modalidadeIntern = modalidadeIntern;
	}

	public List<FatAtoMedicoAih> getListaAma() {

		return this.listaAma;
	}

	public Short getModalidadeIntern() {

		return this.modalidadeIntern;
	}

}
