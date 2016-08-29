package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.math.BigInteger;
import java.util.Date;

public interface RegistroAihComum
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		NU_LOTE("nu_lote"),
		QT_LOTE("qt_lote"),
		APRES_LOTE("apres_lote"),
		SEQ_LOTE("seq_lote"),
		ORG_EMIS_AIH("org_emis_aih"),
		CNES_HOSP("cnes_hosp"),
		MUN_HOSP("mun_hosp"),
		NU_AIH("nu_aih"),
		IDENT_AIH("ident_aih"),
		ESPEC_AIH("espec_aih"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Long getNuLote();

	public abstract Integer getQtLote();

	public abstract Date getApresLote();

	public abstract Integer getSeqLote();

	public abstract String getOrgEmisAih();

	public abstract BigInteger getCnesHosp();

	public abstract Integer getMunHosp();

	public abstract Long getNuAih();

	public abstract Short getIdentAih();

	public abstract Byte getEspecAih();
}
