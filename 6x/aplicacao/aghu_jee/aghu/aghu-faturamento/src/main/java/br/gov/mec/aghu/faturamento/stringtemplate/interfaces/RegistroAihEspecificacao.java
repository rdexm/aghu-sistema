package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioModIntern;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStMudaproc;

public interface RegistroAihEspecificacao
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		MOD_INTERN("mod_intern"),
		SEQ_AIH5("seq_aih5"),
		AIH_PROX("aih_prox"),
		AIH_ANT("aih_ant"),
		DT_EMISSAO("dt_emissao"),
		DT_INTERN("dt_intern"),
		DT_SAIDA("dt_saida"),
		PROC_SOLICITADO("proc_solicitado"),
		ST_MUDAPROC("st_mudaproc"),
		PROC_REALIZADO("proc_realizado"),
		CAR_INTERN("car_intern"),
		MOT_SAIDA("mot_saida"),
		IDENT_MED_SOL("ident_med_sol"),
		DOC_MED_SOL("doc_med_sol"),
		IDENT_MED_RESP("ident_med_resp"),
		DOC_MED_RESP("doc_med_resp"),
		IDENT_DIRCLINICO("ident_dirclinico"),
		DOC_DIRCLINICO("doc_dirclinico"),
		IDENT_AUTORIZ("ident_autoriz"),
		DOC_AUTORIZ("doc_autoriz"),
		DIAG_PRIN("diag_prin"),
		DIAG_SEC("diag_sec"),
		DIAG_COMPL("diag_compl"),
		DIAG_OBITO("diag_obito"),
		COD_SOL_LIB("cod_sol_lib"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract DominioModIntern getModIntern();

	public abstract Short getSeqAih5();

	public abstract Long getAihProx();

	public abstract Long getAihAnt();

	public abstract Date getDtEmissao();

	public abstract Date getDtIntern();

	public abstract Date getDtSaida();

	public abstract Long getProcSolicitado();

	public abstract DominioStMudaproc getStMudaproc();

	public abstract Long getProcRealizado();

	public abstract Byte getCarIntern();

	public abstract Byte getMotSaida();

	public abstract DominioCpfCnsCnpjCnes getIdentMedSol();

	public abstract Long getDocMedSol();

	public abstract DominioCpfCnsCnpjCnes getIdentMedResp();

	public abstract Long getDocMedResp();

	public abstract DominioCpfCnsCnpjCnes getIdentDirclinico();

	public abstract Long getDocDirclinico();

	public abstract DominioCpfCnsCnpjCnes getIdentAutoriz();

	public abstract Long getDocAutoriz();

	public abstract String getDiagPrin();

	public abstract String getDiagSec();

	public abstract String getDiagCompl();

	public abstract String getDiagObito();

	public abstract String getCodSolLib();
}
