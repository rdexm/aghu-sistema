package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExameDeptConvenioId;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ManterExamesMaterialDependenteController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	private static final long serialVersionUID = -3279740850475602157L;
	
	private static final Log LOG = LogFactory.getLog(ManterExamesMaterialDependenteController.class);

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/* Material de analise */
	// Variáveis para controle de edição
	private AelExamesMaterialAnalise examesMaterialAnalise;
	private String sigla;
	private Integer manSeq;
	private Integer cid;
	/* fim das váriaveis de material de análise */

	private AelExamesDependentes examesDependentes;
	private VAelExameMatAnalise aelExameMatAnalise;
	private Byte planoId;
	private Short convenioId;
	private FatConvenioSaudePlano plano;
	private String planoDesc;
	private String convenioDesc;
	private DominioSituacao indSituacaoConvPlano = DominioSituacao.A;
	private AelExameDeptConvenio exameConvenioPlano = new AelExameDeptConvenio();

	private List<AelExameDeptConvenio> listaConveniosPlanosDependentes;
	private List<AelExamesDependentes> listaExamesDependentes;

	/* para excluir */
	private String siglaDependente;
	private int manSeqDependente = 0;
	private boolean desabilitarCodigo = false;
	private AelExameDeptConvenioId exameConvenioIdOriginal;
	private boolean gravouConvPlano;
	
	private boolean editExameDependente;
	private boolean editConvenioPlano;
	
	public enum ManterExamesMaterialDependenteControllerExceptionCode implements BusinessExceptionCode {
		MANTER_EXAMES_MATERIAL_DEPENDENTE_GRAVAR;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() throws ApplicationBusinessException {
	 
		try {

			if (this.gravouConvPlano) {
				return;
			}

			if (StringUtils.isNotBlank(this.sigla) && this.manSeq != null) {
				this.examesMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
			}

			if (this.siglaDependente != null && this.manSeqDependente != 0) {
				/* Busca o exame para edição */
				AelExamesDependentesId id = new AelExamesDependentesId();
				id.setEmaExaSigla(this.sigla);
				id.setEmaManSeq(this.manSeq);
				id.setEmaExaSiglaEhDependente(this.siglaDependente);
				id.setEmaManSeqEhDependente(this.manSeqDependente);

				this.examesDependentes = this.cadastrosApoioExamesFacade.buscarAelExamesDependenteById(id);
				aelExameMatAnalise = examesDependentes.getExameDependente();
				/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*/

				/* Busca os planos/convênios do exame dependente */
				this.listaConveniosPlanosDependentes = this.cadastrosApoioExamesFacade.obterConvenioPlanoDependentes(this.examesDependentes.getId());

			}

			atualizaListaExamesDependentes();

			if (this.examesDependentes == null) {
				this.limpaExameDependente();
				exameConvenioPlano = new AelExameDeptConvenio();
			}

		} finally {
			this.gravouConvPlano = false;
		}
	}
	
	public boolean isExaDeptEmEdicao(AelExamesDependentesId aelExamesDependentesId) {
		
		boolean isEdicao = false;
		
		if (examesDependentes != null && examesDependentes.getId() != null) {
	
			AelExamesDependentesId aelExamesDependentesIdEdicao = examesDependentes.getId();

			if (aelExamesDependentesIdEdicao.equals(aelExamesDependentesId)) {
				isEdicao = true;
			} else {
				isEdicao = false;
			}
		}
		
		return isEdicao;
	}

	public boolean isPlanoConvEmEdicao(AelExameDeptConvenioId aelExameDeptConvenioId) {
		
		boolean isEdicao = false;
		
		if (this.exameConvenioPlano != null && exameConvenioPlano.getId() != null) {
			
			AelExameDeptConvenioId aelExameDeptConvenioIdEdicao = exameConvenioPlano.getId();
			
			if (aelExameDeptConvenioIdEdicao.equals(aelExameDeptConvenioId)) {
				isEdicao = true;
			} else {
				isEdicao = false;
			}
		}
		
		return isEdicao;
	}

	public void removerPlanoConvEmEdicao(AelExameDeptConvenio ExaConv) {
		if (this.listaConveniosPlanosDependentes != null && ExaConv != null) {
			listaConveniosPlanosDependentes.remove(ExaConv);
		}
	}

	/**
	 * Confirma a persisência das informações
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void gravarConvPlano() {
		try {
			int counter = 0;
			if (this.convenioId == null) {
				this.apresentarMsgNegocio("codigoConvenio", Severity.ERROR, CAMPO_OBRIGATORIO, "Código - Convênio");
				counter++;
			}

			if (this.planoId == null) {
				this.apresentarMsgNegocio("codigoPlano", Severity.ERROR, CAMPO_OBRIGATORIO, "Código Plano");
				counter++;
			}

			if ((this.convenioDesc == null || this.convenioDesc.trim().equals("")) || (this.planoDesc == null || this.planoDesc.trim().equals(""))) {
				this.apresentarMsgNegocio("plano", Severity.ERROR, CAMPO_OBRIGATORIO, "Convênio / Plano");
				counter++;
			}

			if (this.indSituacaoConvPlano == null) {
				this.apresentarMsgNegocio("indSituacaoConvPlano", Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
				counter++;
			}

			if (counter > 0) {
				return;
			}

			if (this.listaConveniosPlanosDependentes == null) {
				this.listaConveniosPlanosDependentes = new ArrayList<AelExameDeptConvenio>();
			}

			if (exameConvenioPlano != null && exameConvenioPlano.getId() != null) {
				listaConveniosPlanosDependentes.remove(exameConvenioPlano);
			}

			/* Id do exame convenio */
			AelExameDeptConvenioId exameConvenioId = new AelExameDeptConvenioId();
			exameConvenioId.setExdEmaExaSigla(this.sigla);
			exameConvenioId.setExdEmaManSeq(this.manSeq);

			if (this.aelExameMatAnalise != null && this.aelExameMatAnalise.getId() != null) {
				exameConvenioId.setExdEmaExaSiglaEhDependent(this.aelExameMatAnalise.getId().getExaSigla());
			}

			if (this.aelExameMatAnalise != null && this.aelExameMatAnalise.getId() != null) {
				exameConvenioId.setExdEmaManSeqEhDependente(this.aelExameMatAnalise.getId().getManSeq());
			}

			exameConvenioId.setCspCnvCodigo(this.convenioId);
			exameConvenioId.setCspSeq(this.planoId.shortValue());
			/*-------------------------------*/

			/* Válida se já foi inserido */
			if (!exameConvenioId.equals(this.exameConvenioIdOriginal)) {
				this.cadastrosApoioExamesFacade.validaConvenioPlanoJaInserido(listaConveniosPlanosDependentes, exameConvenioId);
			}
			/* fim da validação */

			/* convênio e plano */
			AelExameDeptConvenio exameConvenio = new AelExameDeptConvenio();
			exameConvenio.setId(exameConvenioId);
			exameConvenio.setPlanoDesc(this.planoDesc);
			exameConvenio.setConvenioDesc(this.convenioDesc);
			exameConvenio.setIndSituacao(this.indSituacaoConvPlano);
			exameConvenio.setRapServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			exameConvenio.setCriadoEm(new Date());
			exameConvenio.setFatConvenioPlano(this.plano);
			/*-------------------------------*/

			if (this.listaConveniosPlanosDependentes.contains(exameConvenio)) {
				this.listaConveniosPlanosDependentes.set(this.listaConveniosPlanosDependentes.indexOf(exameConvenio), exameConvenio);
			} else {
				this.listaConveniosPlanosDependentes.add(exameConvenio);
			}

			limparPlanoConvenio();

			this.gravouConvPlano = true;

		} catch (ApplicationBusinessException e) {
			this.gravouConvPlano = false;
			apresentarExcecaoNegocio(e);
		} finally {
			this.exameConvenioIdOriginal = null;
		}
	}

	private void limparPlanoConvenio() {
		atribuirPlano(null);
		this.exameConvenioPlano = new AelExameDeptConvenio();
		setIndSituacaoConvPlano(DominioSituacao.A);
		
		this.editConvenioPlano = false;
	}

	public void editarExameDependente(AelExamesDependentes itemListaExamesDependente){
		this.setExamesDependentes(itemListaExamesDependente);
		this.listaConveniosPlanosDependentes.clear();
		this.listaConveniosPlanosDependentes.addAll(this.cadastrosApoioExamesFacade.obterConvenioPlanoDependentes(itemListaExamesDependente.getId()));
		aelExameMatAnalise = examesDependentes.getExameDependente();

		limparPlanoConvenio();
		
		this.editExameDependente = true;
	}

	public void editarPlanoConvenio(AelExameDeptConvenio plano) {
		this.exameConvenioIdOriginal = plano.getId();

		AelExameDeptConvenio planoAux = new AelExameDeptConvenio();
		planoAux.setFatConvenioPlano(plano.getFatConvenioPlano());
		planoAux.setIndSituacao(plano.getIndSituacao());
		planoAux.setConvenioDesc(plano.getConvenioDesc());
		planoAux.setId(plano.getId());
		planoAux.setPlanoDesc(plano.getPlanoDesc());

		setPlano(planoAux.getFatConvenioPlano());
		setIndSituacaoConvPlano(planoAux.getIndSituacao());
		setConvenioDesc(planoAux.getConvenioDesc());
		setConvenioId(planoAux.getId().getCspCnvCodigo());
		setPlanoDesc(planoAux.getPlanoDesc());
		setPlanoId(planoAux.getId().getCspSeq().byteValue());
		this.exameConvenioPlano = planoAux;

		this.gravouConvPlano = true;
		
		this.editConvenioPlano = true;
		
	}

	public void confirmar() {
		try {
			
			/*if(!this.gravouConvPlano){
				throw new ApplicationBusinessException(ManterExamesMaterialDependenteControllerExceptionCode.MANTER_EXAMES_MATERIAL_DEPENDENTE_GRAVAR);
			}*/
			
			// Determina o tipo de operação de persistencia
			boolean testEdicao = (this.examesDependentes.getId() == null);

			if (testEdicao) {

				AelExamesDependentesId id = new AelExamesDependentesId();

				id.setEmaExaSigla(this.sigla);
				id.setEmaManSeq(this.manSeq);
				id.setEmaExaSiglaEhDependente(this.aelExameMatAnalise.getId().getExaSigla());
				id.setEmaManSeqEhDependente(this.aelExameMatAnalise.getId().getManSeq());

				this.examesDependentes.setId(id);
				// insere os dependentes
				this.cadastrosApoioExamesFacade.inserirAelExamesDependentes(this.examesDependentes, this.listaConveniosPlanosDependentes);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_EXAME_DEPENDENTE", this.aelExameMatAnalise.getDescricaoUsualExame());
			} else {

				/* Copia os objetos */
				AelExamesDependentes exaDepToUpdate = this.cadastrosApoioExamesFacade.buscarAelExamesDependenteById(this.examesDependentes.getIdAux());
				AelExamesDependentes exaDepAux = new AelExamesDependentes();

				try {
					BeanUtils.copyProperties(exaDepAux, exaDepToUpdate);
				} catch (Exception e) {
					LOG.error("Exceção caputada:", e);
				}

				// remover so a lista dos dependentes
				this.cadastrosApoioExamesFacade.removerListaDependentes(this.examesDependentes);

				// atualiza dependentes
				this.cadastrosApoioExamesFacade.atualizarAelExamesDependentes(this.examesDependentes, exaDepAux);
				// insere novamente os dependentes
				this.cadastrosApoioExamesFacade.inserirExameDeptConvenioEmLote(this.examesDependentes, listaConveniosPlanosDependentes);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_EXAME_DEPENDENTE", this.aelExameMatAnalise.getDescricaoUsualExame());
			}

			atualizaListaExamesDependentes();
			setSiglaDependente(null);
			setManSeqDependente(0);
			this.limpaExameDependente();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.examesDependentes.setId(null);
		}

	}

	private void atualizaListaExamesDependentes() {
		setListaExamesDependentes(this.cadastrosApoioExamesFacade.obterExamesDependentes(this.sigla, this.manSeq));
	}

	private void limpaExameDependente() {
		// Instancia um novo material de análise de exame
		this.examesDependentes = new AelExamesDependentes();
		this.examesDependentes.setIndSituacao(DominioSituacao.A);
		this.examesDependentes.setIndOpcional(DominioSimNao.N);
		this.examesDependentes.setIndCancelaAutomatico(DominioSimNao.S);
		this.examesDependentes.setIndCancLaudoUnico(DominioSimNao.N);

		this.aelExameMatAnalise = null;
		this.listaConveniosPlanosDependentes = null;
		this.editExameDependente = false;
		limparPlanoConvenio();

	}

	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.buscaEXADEPVAelExameMatAnalisePelaSigla((String) objPesquisa);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<VAelExameMatAnalise>();
		}
	}

	/**
	 * Excluir
	 */
	public void excluir(AelExamesDependentes dependenteRemover) {
		try {

			if (dependenteRemover != null) {
				String descricaoExame = dependenteRemover.getExameDependente().getDescricaoExame();
				this.cadastrosApoioExamesFacade.removerAelExamesDependentes(dependenteRemover);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_EXAME_DEPENDENTE", descricaoExame);

				limpaExameDependente();
				atualizaListaExamesDependentes();

			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_EXAME_DEPENDENTE");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			this.atribuirPlano(this.cadastrosApoioExamesFacade.obterPlanoPorId(this.planoId, this.convenioId));
		}
	}

	private void atribuirPlano(FatConvenioSaudePlano plano) {

		if (plano != null) {
			setPlano(plano);
			setConvenioId(plano.getConvenioSaude().getCodigo());
			setPlanoId(plano.getId().getSeq());
			setConvenioDesc(plano.getConvenioSaude().getDescricao());
			setPlanoDesc(plano.getDescricao());

		} else {
			setPlano(null);
			setConvenioId(null);
			setPlanoId(null);
			setConvenioDesc(null);
			setPlanoDesc(null);
		}
	}

	public void atribuirPlano() {
		this.atribuirPlano(this.plano);
	}

	/**
	 * Método que realiza a ação do botão cancelar dos exames dependentes
	 */
	public void cancelar() {
		limpaExameDependente();
		this.exameConvenioPlano = new AelExameDeptConvenio();
		this.siglaDependente = null;
		this.manSeqDependente = 0;
		this.exameConvenioIdOriginal = null;
		this.gravouConvPlano = false;
		atribuirPlano(null);
		
	}

	public String voltar() {
		limpaExameDependente();
		this.exameConvenioPlano = new AelExameDeptConvenio();
		atribuirPlano(null);
		this.siglaDependente = null;
		this.manSeqDependente = 0;
		this.limparParametros();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	private void limparParametros() {

		this.examesMaterialAnalise = null;
		this.sigla = null;
		this.manSeq = null;
		this.cid = null;
		this.examesDependentes = null;
		this.aelExameMatAnalise = null;
		this.planoId = null;
		this.convenioId = null;
		this.plano = null;
		this.planoDesc = null;
		this.convenioDesc = null;
		this.indSituacaoConvPlano = DominioSituacao.A;
		this.exameConvenioPlano = new AelExameDeptConvenio();
		this.listaConveniosPlanosDependentes = null;
		this.listaExamesDependentes = null;
		this.siglaDependente = null;
		this.manSeqDependente = 0;
		this.desabilitarCodigo = false;
		this.exameConvenioIdOriginal = null;
		
		this.editExameDependente = false;
		this.editConvenioPlano = false;
	}

	public void cancelarPlano() {
		this.exameConvenioPlano = new AelExameDeptConvenio();
		this.exameConvenioIdOriginal = null;
		this.gravouConvPlano = true;
		atribuirPlano(null);
		
		this.editConvenioPlano = false;
	}

	public List<AelExameDeptConvenio> getListaConveniosPlanosDependentes() {
		if (this.listaConveniosPlanosDependentes == null) {
			this.listaConveniosPlanosDependentes = new ArrayList<AelExameDeptConvenio>();
		}
		return this.listaConveniosPlanosDependentes;
	}

	public List<AelExamesDependentes> getListaExamesDependentes() {
		return this.listaExamesDependentes;
	}

	/*
	 * Metódo para Suggestion Box abaixo...
	 */

	// Metódo para Suggestion Box de Material de Análise de Exames
	public List<AelMateriaisAnalises> obterMateriaisAnalise(Object parametro) {
		return this.examesFacade.listarAelMateriaisAnalises(parametro);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String parametro) {
		return this.cadastrosApoioExamesFacade.pesquisarConvenioSaudePlanos((String) parametro);
	}

	/*
	 * Getters and Setters abaixo...
	 */

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnalise() {
		return examesMaterialAnalise;
	}

	public void setExamesMaterialAnalise(AelExamesMaterialAnalise examesMaterialAnalise) {
		this.examesMaterialAnalise = examesMaterialAnalise;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public AelExamesDependentes getExamesDependentes() {
		return examesDependentes;
	}

	public void setExamesDependentes(AelExamesDependentes examesDependentes) {
		this.examesDependentes = examesDependentes;
	}

	public VAelExameMatAnalise getAelExameMatAnalise() {
		return aelExameMatAnalise;
	}

	public void setAelExameMatAnalise(VAelExameMatAnalise aelExameMatAnalise) {
		this.aelExameMatAnalise = aelExameMatAnalise;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public String getPlanoDesc() {
		return planoDesc;
	}

	public void setPlanoDesc(String planoDesc) {
		this.planoDesc = planoDesc;
	}

	public DominioSituacao getIndSituacaoConvPlano() {
		return indSituacaoConvPlano;
	}

	public void setIndSituacaoConvPlano(DominioSituacao indSituacaoConvPlano) {
		this.indSituacaoConvPlano = indSituacaoConvPlano;
	}

	public String getConvenioDesc() {
		return convenioDesc;
	}

	public void setConvenioDesc(String convenioDesc) {
		this.convenioDesc = convenioDesc;
	}

	public String getSiglaDependente() {
		return siglaDependente;
	}

	public void setSiglaDependente(String siglaDependente) {
		this.siglaDependente = siglaDependente;
	}

	public int getManSeqDependente() {
		return manSeqDependente;
	}

	public void setManSeqDependente(int manSeqDependente) {
		this.manSeqDependente = manSeqDependente;
	}

	public AelExameDeptConvenio getExameConvenioPlano() {
		return exameConvenioPlano;
	}

	public void setExameConvenioPlano(AelExameDeptConvenio exameConvenioPlano) {
		this.exameConvenioPlano = exameConvenioPlano;
	}

	public void setListaExamesDependentes(List<AelExamesDependentes> listaExamesDependentes) {
		this.listaExamesDependentes = listaExamesDependentes;
	}

	public AelExameDeptConvenioId getExameConvenioIdOriginal() {
		return exameConvenioIdOriginal;
	}

	public void setExameConvenioIdOriginal(AelExameDeptConvenioId exameConvenioIdOriginal) {
		this.exameConvenioIdOriginal = exameConvenioIdOriginal;
	}

	public boolean isGravouConvPlano() {
		return gravouConvPlano;
	}

	public void setGravouConvPlano(boolean gravouConvPlano) {
		this.gravouConvPlano = gravouConvPlano;
	}

	public boolean isEditExameDependente() {
		return editExameDependente;
	}

	public void setEditExameDependente(boolean editExameDependente) {
		this.editExameDependente = editExameDependente;
	}

	public boolean isEditConvenioPlano() {
		return editConvenioPlano;
	}

	public void setEditConvenioPlano(boolean editConvenioPlano) {
		this.editConvenioPlano = editConvenioPlano;
	}
	
}