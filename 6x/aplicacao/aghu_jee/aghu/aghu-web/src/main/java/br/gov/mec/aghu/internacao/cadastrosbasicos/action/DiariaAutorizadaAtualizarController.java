package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinDiariasAutorizadasId;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiariaAutorizadaAtualizarController extends ActionController {

	private static final Log LOG = LogFactory.getLog(DiariaAutorizadaAtualizarController.class);

	private static final String CADASTRO_INTERNACAO = "cadastroInternacao";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8233201342108185239L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private CadastroInternacaoController cadastroInternacaoController;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	// Internação ao qual a diaria pertence
	private AinInternacao internacao;

	/* Diaria a ser editada ID */
	private AinDiariasAutorizadasId diariasAutorizadasId;

	/* Diaria a ser editada */
	private AinDiariasAutorizadas diaria = new AinDiariasAutorizadas();

	/*
	 * Lista de diariar a ser utilizada pelo dataTable e para associar as
	 * diarias autorizadas
	 */
	private List<AinDiariasAutorizadas> listaDiariasOldies ;

	/** Campos de AinDiariasAutorizadas **/
	private Integer codigoInternacao;

	private Short quantDiarias;

	private String senha;

	private String cnrac;

	private boolean operacaoConcluida = false;

	private boolean senhaObrigatoria;
	
	private boolean isAlteracaoTela;

	@PostConstruct
	public void inicio() {
		this.begin(conversation);

		// Metódo chamado pelo pages.xml ao carregar a view
		if (this.codigoInternacao != null) {
			this.internacao = this.cadastrosBasicosInternacaoFacade.obterInternacao(codigoInternacao);
			if (internacao.getDiariasAutorizadas() != null) {
				this.listaDiariasOldies = new ArrayList<AinDiariasAutorizadas>(this.internacao.getDiariasAutorizadas());
				this.senha = null;
				this.cnrac = null;
			} else {
				this.internacao.setDiariasAutorizadas(new ArrayList<AinDiariasAutorizadas>(0));
				this.listaDiariasOldies = new ArrayList<AinDiariasAutorizadas>(0);
			}
		} else {
			this.listaDiariasOldies = new ArrayList<AinDiariasAutorizadas>(0);
		}

		this.senhaObrigatoria = this.senhaDiariaObrigatoria();
		
		this.isAlteracaoTela = false;
	}

	public boolean senhaDiariaObrigatoria() {
		AghParametros parametroConvenioSusPadrao;
		Short codigoSus = null;

		if (this.internacao == null) {
			return true;
		}

		try {
			parametroConvenioSusPadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);

			if (parametroConvenioSusPadrao != null) {
				codigoSus = parametroConvenioSusPadrao.getVlrNumerico().shortValue();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return internacao.getConvenioSaudePlano().getConvenioSaude().getCodigo().equals(codigoSus);
	}

	public void remover(AinDiariasAutorizadas diariaExcluir) {
		for(AinDiariasAutorizadas diaria : this.internacao.getDiariasAutorizadas()){
			if(diariaExcluir.equals(diaria)){
				this.internacao.getDiariasAutorizadas().remove(diaria);		
				break;
			}
		}
		this.isAlteracaoTela = true;
	}

	// Edição de diária caso seja preciso
	public void editarDiaria(AinDiariasAutorizadas diaria) {
		this.cnrac = diaria.getCnrac();
		this.senha = diaria.getSenha();
		this.quantDiarias = diaria.getQuantDiarias();
		this.diaria = diaria;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void associarDiarias() {
		if (this.quantDiarias != null) {
			// Caso estejamos criando uma nova Diaria
			if (this.diaria == null || this.diaria.getId() == null || this.diaria.getId().getSeq() == null) {
				this.diaria = new AinDiariasAutorizadas();
			}

			AinDiariasAutorizadasId id = new AinDiariasAutorizadasId();

			if (this.internacao != null) {
				id.setIntSeq(internacao.getSeq());
			}
			this.diaria.setId(id);
			this.diaria.setCriadoEm(new Date());
			this.diaria.setInternacao(internacao);

			// Setando a matrícula do funcionário, o vinculo, a hora em que foi
			// criado e a internação
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			//Tratamento de proxy "#{_diaria.serMatricula.pessoaFisica.nome}"
			servidorLogado = registroColaboradorFacade.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
					servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
			this.diaria.setSerMatricula(servidorLogado);

			// Atualizando os campos para inserção e edição
			this.diaria.setCnrac(cnrac == null ? null : cnrac);
			this.diaria.setQuantDiarias(quantDiarias == null ? null : quantDiarias);
			this.diaria.setSenha(senha == null ? null : senha);

			// Setando para maiusculas
			if (diaria.getSenha() != null) {
				diaria.setSenha(diaria.getSenha().toUpperCase());
			}
			if (diaria.getCnrac() != null) {
				diaria.setCnrac(diaria.getCnrac().toUpperCase());
			}

			if (this.internacao != null) {
				// && this.internacao.getDiariasAutorizadas() != null
				// &&
				// !this.internacao.getDiariasAutorizadas().contains(diaria)){
				if (internacao.getConvenioSaude() != null && internacao.getConvenioSaude().getGrupoConvenio() != null
						&& DominioGrupoConvenio.S == internacao.getConvenioSaude().getGrupoConvenio() && this.senhaObrigatoria
						&& StringUtils.isBlank(this.senha)) {
					apresentarMsgNegocio(Severity.ERROR, getBundle().getString("AIN_00824"));
					return;
				}
				this.internacao.getDiariasAutorizadas().add(diaria);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICIONAR_DIARIA");
			}
			// Adiciona a diaria a lista que alimenta o grid da tela de
			// atualização.
			// this.listaDiarias.add(diariaAutorizada);

			// Limpando a entidade
			this.cnrac = null;
			this.quantDiarias = null;
			this.senha = null;
			this.diaria = new AinDiariasAutorizadas();
			this.operacaoConcluida = true;
			this.isAlteracaoTela = true;
		} else {
			if (this.quantDiarias == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Nº de Dias");
				// this.getStatusMessages().addToControlFromResourceBundle("txtNumDias",
				// StatusMessage.Severity.ERROR, "CAMPO_OBRIGATORIO",
				// "Nº de Dias");
			}
		}
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * internação
	 */
	public String confirmar() {
		// Persistir ou atualizar os dados
		if (this.listaDiariasOldies != null) {
			try {

				this.cadastrosBasicosInternacaoFacade.atualizarListaDiariasAutorizadas(this.internacao.getDiariasAutorizadas(), listaDiariasOldies);

				if(isAlteracaoTela){
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_DIARIA");
				}

				// Limpando variáveis
				this.cnrac = null;
				this.quantDiarias = null;
				this.senha = null;
				this.diaria = new AinDiariasAutorizadas();
				this.listaDiariasOldies = new ArrayList<AinDiariasAutorizadas>(0);

			} catch (ApplicationBusinessException e) {
				e.getMessage();
				apresentarExcecaoNegocio(e);
				return null;
			}
		}

		cadastroInternacaoController.setAipPacCodigo(internacao.getPaciente().getCodigo());
		cadastroInternacaoController.setRetornouTelaAssociada(true);

		return CADASTRO_INTERNACAO;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * internação - atualizar diária.
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		this.cnrac = null;
		this.quantDiarias = null;
		this.senha = null;
		this.diaria = new AinDiariasAutorizadas();
		this.listaDiariasOldies = new ArrayList<AinDiariasAutorizadas>(0);

		cadastroInternacaoController.setAipPacCodigo(internacao.getPaciente().getCodigo());
		cadastroInternacaoController.setRetornouTelaAssociada(true);

		return CADASTRO_INTERNACAO;
	}

	// #################### getters and setters################################
	public AinDiariasAutorizadasId getDiariasAutorizadasId() {
		return diariasAutorizadasId;
	}

	public void setDiariasAutorizadasId(AinDiariasAutorizadasId diariasAutorizadasId) {
		this.diariasAutorizadasId = diariasAutorizadasId;
	}

	public AinDiariasAutorizadas getDiariasAutorizadas() {
		return diaria;
	}

	public void setDiariasAutorizadas(AinDiariasAutorizadas diariasAutorizadas) {
		this.diaria = diariasAutorizadas;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public List<AinDiariasAutorizadas> getListaDiarias() {
		return listaDiariasOldies;
	}

	public void setListaDiarias(List<AinDiariasAutorizadas> listaDiarias) {
		this.listaDiariasOldies = listaDiarias;
	}

	public boolean isOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public void setCodigoInternacao(Integer codigoInternacao) {
		this.codigoInternacao = codigoInternacao;
	}

	public Integer getCodigoInternacao() {
		return codigoInternacao;
	}

	public Short getQuantDiarias() {
		return quantDiarias;
	}

	public void setQuantDiarias(Short quantDiarias) {
		this.quantDiarias = quantDiarias;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCnrac() {
		return cnrac;
	}

	public void setCnrac(String cnrac) {
		this.cnrac = cnrac;
	}

	public boolean isSenhaObrigatoria() {
		return senhaObrigatoria;
	}

	public void setSenhaObrigatoria(boolean senhaObrigatoria) {
		this.senhaObrigatoria = senhaObrigatoria;
	}

}