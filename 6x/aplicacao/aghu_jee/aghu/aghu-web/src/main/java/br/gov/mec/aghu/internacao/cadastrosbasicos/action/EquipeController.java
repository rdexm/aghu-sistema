package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe responsável por controlar as ações do criação e edição de equipes.
 */
public class EquipeController extends ActionController {

	private static final long serialVersionUID = 2997725060465944405L;

	private static final String REDIRECIONA_PESQUISAR_EQUIPE = "equipeList";

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * Equipe a ser criada/editada
	 */
	private AghEquipes aghEquipe;

	/**
	 * Responsável selecionado na criação/edição de equipes.
	 */
	private RapServidoresVO responsavelEquipe;

	/**
	 * Lista de profissionais que pertencem a equipe.
	 */
	private List<RapServidoresVO> profissionaisEquipe = new ArrayList<RapServidoresVO>(0);

	/**
	 * Profissional selecionado para pertencer a equipe.
	 */
	private RapServidoresVO profissional;

	private static final Comparator<RapServidoresVO> COMPARATOR_PROFISSIONAIS_EQUIPE = new Comparator<RapServidoresVO>() {

		@Override
		public int compare(RapServidoresVO o1, RapServidoresVO o2) {
			return o1.getNome().toUpperCase().compareTo(o2.getNome().toUpperCase());
		}

	};

	private void limpar() {
		this.aghEquipe = new AghEquipes();
		this.aghEquipe.setIndSituacao(DominioSituacao.A);
		this.responsavelEquipe = null;
		this.profissional = null;
		this.profissionaisEquipe = new ArrayList<RapServidoresVO>();
	}

	public void inicio() {
	 

		if (this.aghEquipe != null) {
			this.profissional = null;
		} else {
			this.aghEquipe = new AghEquipes();
			this.aghEquipe.setIndSituacao(DominioSituacao.A);
		}

		RapServidores responsavel = this.aghEquipe.getProfissionalResponsavel();
		if (responsavel != null) {
			responsavelEquipe = new RapServidoresVO(responsavel.getId().getVinCodigo(), responsavel.getId().getMatricula(), responsavel
					.getPessoaFisica().getNome());
		}

		this.profissionaisEquipe = new ArrayList<RapServidoresVO>();
		List<AghProfissionaisEquipe> listaProfissionaisEquipe = aghuFacade.pesquisarServidoresPorEquipe(aghEquipe.getSeq());
		for (AghProfissionaisEquipe profissionalEquipe : listaProfissionaisEquipe) {
			this.profissionaisEquipe.add(new RapServidoresVO(profissionalEquipe.getServidor().getId().getVinCodigo(), profissionalEquipe
					.getServidor().getId().getMatricula(), profissionalEquipe.getServidor().getPessoaFisica().getNome()));
		}

		Collections.sort(this.profissionaisEquipe, COMPARATOR_PROFISSIONAIS_EQUIPE);
	
	}

	public void adicionaProfissionalEquipe() {
		if (this.profissional != null && !this.profissionaisEquipe.contains(this.profissional)) {
			this.profissionaisEquipe.add(this.profissional);
			Collections.sort(this.profissionaisEquipe, COMPARATOR_PROFISSIONAIS_EQUIPE);
		}
		this.profissional = null;
	}

	public void removerProfissionalEquipe(RapServidoresVO profissional) {
		this.profissionaisEquipe.remove(profissional);
	}

	public void limparResponsavel() {
		this.responsavelEquipe = null;
	}

	public boolean isMostrarLinkExcluirResponsavel() {
		return this.responsavelEquipe != null;
	}

	public void limparProfissional() {
		this.profissional = null;
	}

	public boolean isMostrarLinkExcluirProfissional() {
		return this.profissional != null;
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * Equipe.
	 */
	public String confirmar() {
		Boolean ehEdicao = aghEquipe.getSeq() != null;

		try {
			if (this.responsavelEquipe != null) {
				RapServidores responsavel = registroColaboradorFacade.obterRapServidor(new RapServidoresId(
						responsavelEquipe.getMatricula(), responsavelEquipe.getVinculo()));

				this.aghEquipe.setProfissionalResponsavel(responsavel);
				// Retira os espaços do meio do nome
				this.aghEquipe.setNome(retirarEspacosMeio(aghEquipe.getNome()));

				// Tarefa 659 - deixar todos textos das entidades em caixa alta
				// via toUpperCase()
				transformarTextosCaixaAlta();

				this.aghuFacade.persistirEquipe(this.aghEquipe, profissionaisEquipe);

				if (ehEdicao) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_EQUIPE", this.aghEquipe.getNome());
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_EQUIPE", this.aghEquipe.getNome());
				}
			} else {
				apresentarMsgNegocio(Severity.INFO, "RESPONSAVEL_EQUIPE_OBRIGATORIO");
				return null;
			}
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}

		limpar();

		return REDIRECIONA_PESQUISAR_EQUIPE;
	}

	public String cancelar() {
		limpar();
		return REDIRECIONA_PESQUISAR_EQUIPE;
	}

	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(String parametro) {
		return registroColaboradorFacade.pesquisarRapServidoresVOPorCodigoDescricao(parametro);
	}

	/*
	 * Retira os espaços do meio do nome
	 */
	public String retirarEspacosMeio(String nome) {
		nome = StringUtil.trim(nome);

		StringBuffer nomeSemEspacos = new StringBuffer();
		for (int i = 0; i < nome.length(); i++) {
			if (nome.charAt(i) == ' ') {
				if (i + 1 < nome.length()) {
					if (nome.charAt(i + 1) != ' ') {
						nomeSemEspacos.append(nome.charAt(i));
					}
				}
			} else {
				nomeSemEspacos.append(nome.charAt(i));
			}
		}
		return nomeSemEspacos.toString();
	}

	// ### GETs e SETs ###

	private void transformarTextosCaixaAlta() {
		this.aghEquipe.setNome(this.aghEquipe.getNome() == null ? null : this.aghEquipe.getNome().toUpperCase());
	}

	public void limparProfissionalResponsavel() {
		if (this.aghEquipe != null && this.aghEquipe.getProfissionalResponsavel() != null) {
			this.aghEquipe.setProfissionalResponsavel(null);
		}
	}

	public boolean isMostrarLinkExcluirProfissionalResponsavel() {
		return (this.aghEquipe != null && this.aghEquipe.getProfissionalResponsavel() != null);
	}

	public AghEquipes getAghEquipe() {
		return aghEquipe;
	}

	public void setAghEquipe(AghEquipes aghEquipe) {
		this.aghEquipe = aghEquipe;
	}

	public RapServidoresVO getResponsavelEquipe() {
		return responsavelEquipe;
	}

	public void setResponsavelEquipe(RapServidoresVO responsavelEquipe) {
		this.responsavelEquipe = responsavelEquipe;
	}

	public List<RapServidoresVO> getProfissionaisEquipe() {
		return profissionaisEquipe;
	}

	public void setProfissionaisEquipe(List<RapServidoresVO> profissionaisEquipe) {
		this.profissionaisEquipe = profissionaisEquipe;
	}

	public RapServidoresVO getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidoresVO profissional) {
		this.profissional = profissional;
	}
}