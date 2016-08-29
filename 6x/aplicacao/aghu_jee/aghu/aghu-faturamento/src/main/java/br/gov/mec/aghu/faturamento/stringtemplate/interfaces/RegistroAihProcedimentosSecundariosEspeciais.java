package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioInEquipe;

public interface RegistroAihProcedimentosSecundariosEspeciais
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		IN_PROF("in_prof"),
		IDENT_PROF("ident_prof"),
		CBO_PROF("cbo_prof"),
		IN_EQUIPE("in_equipe"),
		IN_SERVICO("in_servico"),
		IDENT_SERVICO("ident_servico"),
		IN_EXECUTOR("in_executor"),
		IDENT_EXECUTOR("ident_executor"),
		COD_PROCED("cod_proced"),
		QTD_PROCED("qtd_proced"),
		CMPT("cmpt"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract DominioCpfCnsCnpjCnes getInProf();

	public abstract Long getIdentProf();

	public abstract Integer getCboProf();

	public abstract DominioInEquipe getInEquipe();

	public abstract DominioCpfCnsCnpjCnes getInServico();

	public abstract Long getIdentServico();

	public abstract DominioCpfCnsCnpjCnes getInExecutor();

	public abstract Long getIdentExecutor();

	public abstract Long getCodProced();

	public abstract Short getQtdProced();

	public abstract String getCmpt();
}
