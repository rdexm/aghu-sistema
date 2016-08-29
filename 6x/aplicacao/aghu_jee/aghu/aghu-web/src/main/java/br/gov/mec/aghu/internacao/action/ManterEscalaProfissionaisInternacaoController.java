package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinEscalasProfissionalIntId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ManterEscalaProfissionaisInternacaoController extends ActionController {

	private static final long serialVersionUID = -6547934137880315294L;
	private static final String REDIRECT_PESQUISAR_ESCALA_PROFISSIONAIS_INTERNACAO = "escalaInternacao";

	@EJB
	private IInternacaoFacade internacaoFacade;

	private AinEscalasProfissionalInt ainEscalasProfissionalInt;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * Campos carregados através da tela anterior. Escala de Profissionais da
	 * Internação - Pesquisar Profissional.
	 */
	private Integer pecPreSerMatricula;
	private Integer pecPreSerVinCodigo;
	private Integer pecCnvCodigo;
	private Integer pecPreEspSeq;
	private Date dtInicio;

	/**
	 * Campos carregados através da tela anterior. Utilizados somente para
	 * informação na tela, portanto não são persistidos.
	 */
	private String registroServidor;
	private String nomeServidor;
	private String siglaEspecialidade;
	private String descricaoConvenio;

	/**
	 * Vínculo do Substituto
	 */
	private Integer vinculoSubstituto;

	/**
	 * Matrícula do Substituto
	 */
	private Integer matriculaSubstituto;

	private RapServidores rapServidoresSubstituto;

	/**
	 * LOV - Campo de pesquisa para Matricula ou Nome do Substituto na LOV.
	 */
	private Object substitutoPesquisaLOV;

	/**
	 * Utilizado como parâmetro para navegação entre as telas da escala. Indica
	 * de qual tela veio a chamada para poder retornar posteriormente para a
	 * tela anterior.
	 */
	private String cameFrom = "";

	/**
	 * Utilizado na lista de Indicação de CTI
	 */
	private DominioSimNao indCti;

	/**
	 * LOV - Listagem de Substitutos da escala de um profissional.
	 */
//	private List<RapServidores> listaSubstitutos = new ArrayList<RapServidores>();

	/**
	 * Indica se foi solitado uma inclusão ou alteração de escala.
	 */
	private boolean incluirEscala;

	/**
	 * Registro Profissional retornado da LOV
	 */
	private String regProfSubstituto;

	public void inicio() {
		rapServidoresSubstituto = null;
		this.matriculaSubstituto = null;
		this.vinculoSubstituto = null;
		this.regProfSubstituto = "";

		if (isIncluirEscala()) {

			this.ainEscalasProfissionalInt = new AinEscalasProfissionalInt();

			final AinEscalasProfissionalIntId id = new AinEscalasProfissionalIntId();

			id.setPecPreSerMatricula(pecPreSerMatricula);
			id.setPecPreSerVinCodigo(pecPreSerVinCodigo.shortValue());
			id.setPecCnvCodigo(pecCnvCodigo.shortValue());
			id.setPecPreEspSeq(pecPreEspSeq.shortValue());

			this.ainEscalasProfissionalInt.setId(id);

			this.indCti = DominioSimNao.N;

		} else {
			ainEscalasProfissionalInt = internacaoFacade
					.obterProfissionalEscala(this.pecPreSerMatricula,
							this.pecPreSerVinCodigo, this.pecCnvCodigo,
							this.pecPreEspSeq, this.dtInicio);

			if (ainEscalasProfissionalInt.getServidorProfessor() != null
					&& ainEscalasProfissionalInt.getServidorProfessor().getId() != null) {
				this.rapServidoresSubstituto = ainEscalasProfissionalInt.getServidorProfessor();
				this.matriculaSubstituto = ainEscalasProfissionalInt.getServidorProfessor().getId().getMatricula();
				this.vinculoSubstituto = ainEscalasProfissionalInt.getServidorProfessor().getId().getVinCodigo().intValue();
				this.buscarSubstituto();
			}

			if (DominioSimNao.S.name().equals(
					ainEscalasProfissionalInt.getIndAtuaCti())) {
				this.indCti = DominioSimNao.S;
			}

			if (DominioSimNao.N.name().equals(
					ainEscalasProfissionalInt.getIndAtuaCti())) {
				this.indCti = DominioSimNao.N;
			}
		}

		if (this.ainEscalasProfissionalInt == null) {
			this.ainEscalasProfissionalInt = new AinEscalasProfissionalInt();
		}
	}

	/**
	 * Busca Servidor Substituto pelo vínculo e matrícula.
	 */
	public void buscarSubstituto() {

		rapServidoresSubstituto = null;
		regProfSubstituto = "";

		if (vinculoSubstituto != null && matriculaSubstituto != null) {
			rapServidoresSubstituto = registroColaboradorFacade
					.obterSubstituto(vinculoSubstituto.shortValue(),
							matriculaSubstituto);
			
			if (rapServidoresSubstituto != null) {
				final Set<RapQualificacao> qualificacao = new HashSet<RapQualificacao>(registroColaboradorFacade.pesquisarQualificacoes(rapServidoresSubstituto
						.getPessoaFisica()));
				//final Set<RapQualificacao> qualificacao = rapServidoresSubstituto
				//		.getPessoaFisica().getQualificacoes();
	
				for (final RapQualificacao qualif : qualificacao) {
					if (StringUtils.isNotBlank(qualif.getNroRegConselho())) {
						regProfSubstituto = qualif.getNroRegConselho();
					}
				}
			}
		}

		if (rapServidoresSubstituto == null
				|| rapServidoresSubstituto.getId() == null) {
			rapServidoresSubstituto = new RapServidores();
			regProfSubstituto = "";
		}
	}

	public List<RapServidores> pesquisarSubstitutos(final String pmr) {
		List<RapServidores> listaSubstitutos = new ArrayList<RapServidores>();
		
		try {

			if (ainEscalasProfissionalInt.getDtFim() != null) {
				
				// só procura substitutos se informar data de fim
				final Date dtEscalaDisponivel = DateUtil.adicionaDias(ainEscalasProfissionalInt.getDtFim(), 1);
				
				listaSubstitutos = internacaoFacade.pesquisarProfissionaisSubstitutos(pecPreEspSeq.shortValue(), pecCnvCodigo.shortValue(),
																		  dtEscalaDisponivel, substitutoPesquisaLOV
																		 );
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_INFORME_DATA_FIM_ANTES_SUBSTITUTO");
			}

		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return listaSubstitutos;
	}
	
	public void populaFields(){
		vinculoSubstituto = null;
		regProfSubstituto = null;
		
		if(rapServidoresSubstituto != null){
			vinculoSubstituto = rapServidoresSubstituto.getId().getVinCodigo().intValue();	
			try {
				RapPessoasFisicas rapPessoasFisicas = registroColaboradorFacade.obterPessoaFisica(rapServidoresSubstituto.getPessoaFisica().getCodigo());
				final List<RapQualificacao> qualificacao = registroColaboradorFacade.pesquisarQualificacoes(rapPessoasFisicas);
			
				for (final RapQualificacao qualif : qualificacao) {
					if (StringUtils.isNotBlank(qualif.getNroRegConselho())) {
						regProfSubstituto = qualif.getNroRegConselho();
					}
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		}
	}

	public String salvar() {

		try {

			if (isIncluirEscala()) {
				final AghProfEspecialidadesId profEspecialidadesId = new AghProfEspecialidadesId();

				profEspecialidadesId.setEspSeq(pecPreEspSeq.shortValue());
				profEspecialidadesId.setSerMatricula(pecPreSerMatricula);
				profEspecialidadesId.setSerVinCodigo(pecPreSerVinCodigo
						.shortValue());

				ainEscalasProfissionalInt
						.setProfEspecialidade(cadastrosBasicosInternacaoFacade
								.obterProfEspecialidades(profEspecialidadesId));

				// substituto
				if (rapServidoresSubstituto != null && rapServidoresSubstituto.getId() != null) {
					ainEscalasProfissionalInt
							.setServidorProfessor(rapServidoresSubstituto);
				}


				if (vinculoSubstituto != null && matriculaSubstituto != null
						&& rapServidoresSubstituto.getId() == null) {
					this.buscarSubstituto();
				}

				ainEscalasProfissionalInt.setIndAtuaCti(this.indCti.toString());

				internacaoFacade
						.incluirEscala(ainEscalasProfissionalInt);

				this.apresentarMsgNegocio(Severity.INFO,
						"Escala incluída com sucesso!");

			} else {

				// substituto
				if (rapServidoresSubstituto != null && rapServidoresSubstituto.getId() != null) {
					ainEscalasProfissionalInt
							.setServidorProfessor(rapServidoresSubstituto);
				} else {
					ainEscalasProfissionalInt.setServidorProfessor(null);
				}

				if (vinculoSubstituto != null && matriculaSubstituto != null
						&& rapServidoresSubstituto != null && rapServidoresSubstituto.getId() == null) {
					this.buscarSubstituto();
				}

				ainEscalasProfissionalInt.setIndAtuaCti(this.indCti.toString());

				internacaoFacade
						.alterarEscala(ainEscalasProfissionalInt);

				this.apresentarMsgNegocio(Severity.INFO,"Escala alterada com sucesso!");
			}

		} catch (final BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}
		return REDIRECT_PESQUISAR_ESCALA_PROFISSIONAIS_INTERNACAO;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela.
	 */
	public String cancelar() {
		if (cameFrom != null && cameFrom.equalsIgnoreCase("escalaInternacao")) {
			return REDIRECT_PESQUISAR_ESCALA_PROFISSIONAIS_INTERNACAO;
		}
		return null;
	}

	public String getRegistroServidor() {
		return registroServidor;
	}

	public void setRegistroServidor(final String registroServidor) {
		this.registroServidor = registroServidor;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(final String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(final String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(final String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public AinEscalasProfissionalInt getAinEscalasProfissionalInt() {
		return ainEscalasProfissionalInt;
	}

	public void setAinEscalasProfissionalInt(
			final AinEscalasProfissionalInt ainEscalasProfissionalInt) {
		this.ainEscalasProfissionalInt = ainEscalasProfissionalInt;
	}

	public Integer getVinculoSubstituto() {
		return vinculoSubstituto;
	}

	public void setVinculoSubstituto(final Integer vinculoSubstituto) {
		this.vinculoSubstituto = vinculoSubstituto;
	}

	public Integer getMatriculaSubstituto() {
		return matriculaSubstituto;
	}

	public void setMatriculaSubstituto(final Integer matriculaSubstituto) {
		this.matriculaSubstituto = matriculaSubstituto;
	}

	public RapServidores getRapServidoresSubstituto() {
		return rapServidoresSubstituto;
	}

	public void setRapServidoresSubstituto(final RapServidores rapServidoresSubstituto) {
		this.rapServidoresSubstituto = rapServidoresSubstituto;
	}

	public Object getSubstitutoPesquisaLOV() {
		return substitutoPesquisaLOV;
	}

	public void setSubstitutoPesquisaLOV(final Object substitutoPesquisaLOV) {
		this.substitutoPesquisaLOV = substitutoPesquisaLOV;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Integer getPecPreSerMatricula() {
		return pecPreSerMatricula;
	}

	public void setPecPreSerMatricula(final Integer pecPreSerMatricula) {
		this.pecPreSerMatricula = pecPreSerMatricula;
	}

	public Integer getPecCnvCodigo() {
		return pecCnvCodigo;
	}

	public void setPecCnvCodigo(final Integer pecCnvCodigo) {
		this.pecCnvCodigo = pecCnvCodigo;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(final Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Integer getPecPreSerVinCodigo() {
		return pecPreSerVinCodigo;
	}

	public void setPecPreSerVinCodigo(final Integer pecPreSerVinCodigo) {
		this.pecPreSerVinCodigo = pecPreSerVinCodigo;
	}

	public Integer getPecPreEspSeq() {
		return pecPreEspSeq;
	}

	public void setPecPreEspSeq(final Integer pecPreEspSeq) {
		this.pecPreEspSeq = pecPreEspSeq;
	}

	public DominioSimNao getIndCti() {
		return indCti;
	}

	public void setIndCti(final DominioSimNao indCti) {
		this.indCti = indCti;
	}

	public boolean isIncluirEscala() {
		return incluirEscala;
	}

	public void setIncluirEscala(final boolean incluirEscala) {
		this.incluirEscala = incluirEscala;
	}

	public String getRegProfSubstituto() {
		return regProfSubstituto;
	}

	public void setRegProfSubstituto(final String regProfSubstituto) {
		this.regProfSubstituto = regProfSubstituto;
	}

}
