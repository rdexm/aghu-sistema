package br.gov.mec.aghu.faturamento.vo;

import java.math.BigInteger;
import java.util.List;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatEspelhoAih;

public class EvtGeraAaqParcialAihRegCivilVO
		extends
			EvtGeraAaqParcialAihGeralVO {

	public static class AamPacVO {

		private final FatAtoMedicoAih aam;
		private final AipPacientes pac;

		public AamPacVO(final FatAtoMedicoAih aam, final AipPacientes pac) {

			super();

			this.aam = aam;
			this.pac = pac;
		}

		public FatAtoMedicoAih getAam() {

			return this.aam;
		}

		public AipPacientes getPac() {

			return this.pac;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3606367478655107454L;

	private final List<AamPacVO> listaAamPac;

	public EvtGeraAaqParcialAihRegCivilVO(
			final FatEspelhoAih eai,
			final Short tahSeq,
			final String orgLocRec, 
			final BigInteger cnes,
			final String cgcHosp, 
			final Integer codMunicipio,
			final String cpfDirClinico,
			final List<AamPacVO> listaAamPac, 
			final BigInteger numeroCNSPaciente) {

		super(
				eai, tahSeq, orgLocRec, cnes, cgcHosp, codMunicipio, cpfDirClinico, numeroCNSPaciente);

		this.listaAamPac = listaAamPac;
	}

	public List<AamPacVO> getListaAamPac() {

		return this.listaAamPac;
	}
}
