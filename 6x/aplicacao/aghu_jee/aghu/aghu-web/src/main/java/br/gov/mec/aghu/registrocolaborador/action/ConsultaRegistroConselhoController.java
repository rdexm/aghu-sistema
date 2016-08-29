package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;



public class ConsultaRegistroConselhoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final long serialVersionUID = 3723172507835761325L;
	
	private static final Enum[] fetchArgsLeftJoin = { RapQualificacao.Fields.TIPO_QUALIFICACAO,
			RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA, RapQualificacao.Fields.PESSOA_FISICA_ALIAS,
			RapQualificacao.Fields.CONSELHO_PROFISSIONAL };

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapQualificacao qualificacao = new RapQualificacao();
	private List<RapServidores> listaServidores = new ArrayList<RapServidores>();

	private Integer codigoPessoa;
	private Short sequenciaQualificacao;

	public String detalhar() {
		
		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas(codigoPessoa);
		RapQualificacoesId id = new RapQualificacoesId(pessoaFisica, sequenciaQualificacao);
		qualificacao = registroColaboradorFacade.obterQualificacao(id, null, fetchArgsLeftJoin);
		listaServidores = registroColaboradorFacade.pesquisarRapServidoresPorCodigoPessoa(codigoPessoa);
		Collections.sort(listaServidores, new ServidoresComparator());
		
		return "detalharRegistroConselho";
	}

	private void limparCadastro() {
		qualificacao = new RapQualificacao();
		setListaServidores(new ArrayList<RapServidores>());
		codigoPessoa = null;
		sequenciaQualificacao = null;
	}

	public String voltar() {
		limparCadastro();
		return "pesquisarRegistroConselho";
	}

	public RapQualificacao getQualificacao() {
		return qualificacao;
	}

	public void setQualificacao(RapQualificacao qualificacao) {
		this.qualificacao = qualificacao;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Short getSequenciaQualificacao() {
		return sequenciaQualificacao;
	}

	public void setSequenciaQualificacao(Short sequencia) {
		this.sequenciaQualificacao = sequencia;
	}

	public void setListaServidores(List<RapServidores> listaServidores) {
		this.listaServidores = listaServidores;
	}

	public List<RapServidores> getListaServidores() {
		return listaServidores;
	}

	private class ServidoresComparator implements Comparator<RapServidores> {

		@Override
		public int compare(RapServidores servidor1, RapServidores servidor2) {
			if (DominioSituacao.A.equals(servidor1.getVinculo().getIndSituacao()) 
					&& !DominioSituacao.A.equals(servidor2.getVinculo().getIndSituacao())) {
				return -1;
			} else if (DominioSituacao.A.equals(servidor2.getVinculo().getIndSituacao()) 
					&& !DominioSituacao.A.equals(servidor1.getVinculo().getIndSituacao())) {
				return 1;
			}
			if (! servidor1.getId().getVinCodigo().equals(servidor2.getId().getVinCodigo())) {
				return servidor1.getId().getVinCodigo().compareTo(servidor2.getId().getVinCodigo());
			}
			if (servidor1.getDtFimVinculo() == null && servidor2.getDtFimVinculo() == null) {
				return servidor1.getDtInicioVinculo().compareTo(servidor2.getDtInicioVinculo());
			} else if (servidor1.getDtFimVinculo() == null) {
				return -1;
			} else if (servidor2.getDtFimVinculo() == null) {
				return 1;
			}
			return servidor1.getDtFimVinculo().compareTo(servidor2.getDtFimVinculo());
		}
		
	}
}