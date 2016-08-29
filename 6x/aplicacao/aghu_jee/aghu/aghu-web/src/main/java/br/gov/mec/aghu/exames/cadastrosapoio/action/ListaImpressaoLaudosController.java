package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.patologia.action.RelatorioLaudoUnicoPdfController;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ListaImpressaoLaudosController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<VAelApXPatologiaAghu> dataModel;
	
	private static final String PAGES_EXAMES_RELATORIO_LAUDO_UNICO_PDF = "exames-relatorioLaudoUnicoPdf";

	private static final long serialVersionUID = -7102047377830610820L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AelPatologista residenteResp;
	private MedicoSolicitanteVO medicoSolic;

	private AelPatologista patologistaResp;
	private AelConfigExLaudoUnico exame;

	private AelExameAp material;
	private DominioConvenioExameSituacao convenio;

	private final DominioSituacaoExamePatologia DOMINIO_SITUACAO_EXAME_PATOLOGIA = DominioSituacaoExamePatologia.LA;
	private DominioSimNao laudoImpresso;
	private Long numeroAp;
	private String nroApSelecionado;
	private Date dtDe;
	private Date dtAte;

	private String siglaConselhoProfissional;

	private Integer vAelApXPatologiaAghuCount;
	
	@Inject
	private ConsultarResultadosNotaAdicionalController resultadosNotaAdicionalController;
	
	@Inject
	private RelatorioLaudoUnicoPdfController relatorioLaudoUnicoPdfController;
	
	/**
	 * Inicializa valores defaults dos principais campos da tela
	 */
	public void iniciar() {
		if (laudoImpresso == null) {
			laudoImpresso = DominioSimNao.N;
		}

		if (siglaConselhoProfissional == null) {
			try {
				siglaConselhoProfissional = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONSELHO_PROFISSIONAL_MED_SOLIC).getVlrTexto();

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		if (this.dataModel.getPesquisaAtiva()) {
			return;
		}

		if (dtDe == null && dtAte == null) {
			inicializarDatas();
		}
				
		inicializarResidentePatologista();
 
		pesquisar();
		
		if (this.dataModel.getEmpty()){
			this.dataModel.limparPesquisa();
		}	
	}
	
	private void inicializarResidentePatologista(){
		if (residenteResp == null && patologistaResp == null) {
			RapServidores servidorLogado = null;
			try {
				servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			} catch (ApplicationBusinessException e) {
				servidorLogado = null;
			}

			if (residenteResp == null) {
				residenteResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.R);
			}

			if (patologistaResp == null) {
				patologistaResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.C, DominioFuncaoPatologista.P);
			}
		}		
	}

	private void inicializarDatas() {
		try {
			final Integer periodoDias = (this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERIODO_REFERENCIA_LISTA_EXAMES_AP).getVlrNumerico().intValue() * -1); // Negativo

			dtAte = new Date();
			dtDe = DateUtil.adicionaDias(new Date(), periodoDias);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String visualizarRelatorio(Long luxSeq, String nomePac){
		AelExameAp exameAP = examesPatologiaFacade.obterAelExameApPorSeq(luxSeq);
		final AelItemSolicitacaoExameLaudoUnicoVO itemVo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(exameAP, false);
		
		if(itemVo != null){
			Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
			Integer soeSeq = itemVo.getSoeSeq();
			Short seqp = itemVo.getSeqp();
			solicitacoes.put(soeSeq, new Vector<Short>());
			solicitacoes.get(soeSeq).add(seqp);
		
			relatorioLaudoUnicoPdfController.setSolicitacoes(solicitacoes);
			relatorioLaudoUnicoPdfController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
			relatorioLaudoUnicoPdfController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
			try {
				examesPatologiaFacade.atualizarImpressao(luxSeq);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			this.dataModel.reiniciarPaginator();
			
			return PAGES_EXAMES_RELATORIO_LAUDO_UNICO_PDF;
		}else{
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_LAUDO_NAO_ENCONTRADO");
			return null;
		}		
	}

	public String imprimirRelatorio(Long luxSeq, Long nroAps, Integer nroVias, Integer lu2Seq) {		
		try {						
			AelExameAp exameAP = examesPatologiaFacade.obterAelExameApPorChavePrimaria(luxSeq);
			final AelItemSolicitacaoExameLaudoUnicoVO itemVo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(exameAP, false);
			
			if(itemVo != null){
				Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
				Integer soeSeq = itemVo.getSoeSeq();
				Short seqp = itemVo.getSeqp();
				solicitacoes.put(soeSeq, new Vector<Short>());
				solicitacoes.get(soeSeq).add(seqp);
			
				resultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
				resultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
				resultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
				resultadosNotaAdicionalController.directPrint(null);
				examesPatologiaFacade.atualizarImpressao(luxSeq);
				this.dataModel.reiniciarPaginator();
			}else{
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_LAUDO_NAO_ENCONTRADO");
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void pesquisar() {
		if (numeroAp != null) {
			this.dataModel.reiniciarPaginator();

		} else {
			if (DateUtil.validaDataMenor(dtAte, dtDe)) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_LISTA_REALIZACAO_EXAMES_PATOLOGIA_PERIODO_INVALIDO");
				return;
			}

			this.dataModel.reiniciarPaginator();
		}
	}

	public void limpar() {
		residenteResp = null;
		medicoSolic = null;
		patologistaResp = null;
		exame = null;
		material = null;
		convenio = null;
		laudoImpresso = null;
		numeroAp = null;
		this.dataModel.limparPesquisa();
		inicializarDatas();
		inicializarResidentePatologista();
	}

	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(String value) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value),pesquisarAelConfigExLaudoUnicoCount(value));
	}

	public Long pesquisarAelConfigExLaudoUnicoCount(String value) {
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value);
	}

	public List<AelPatologista> pesquisarPatologistaResponsavel(String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncao((String) filtro, DominioFuncaoPatologista.P, DominioFuncaoPatologista.C),pesquisarPatologistaResponsavelCount(filtro));
	}

	public Long pesquisarPatologistaResponsavelCount(String filtro) {
		return examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.P, DominioFuncaoPatologista.C);
	}

	public List<AelPatologista> pesquisarResidenteResponsavel(String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncao((String) filtro, DominioFuncaoPatologista.R),pesquisarResidenteResponsavelCount(filtro));
	}

	public Long pesquisarResidenteResponsavelCount(String filtro) {
		return examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.R);
	}

	public List<MedicoSolicitanteVO> pesquisarMedicosSolicitantesVO(String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarMedicosSolicitantesVO((String) filtro, siglaConselhoProfissional),pesquisarMedicosSolicitantesVOCount(filtro));
	}

	public Integer pesquisarMedicosSolicitantesVOCount(String filtro) {
		return examesPatologiaFacade.pesquisarMedicosSolicitantesVOCount((String) filtro, siglaConselhoProfissional);
	}

	public List<AelExameAp> pesquisarAelExameApPorMateriais(final String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelExameApPorMateriais((String) filtro),pesquisarAelExameApPorMateriaisCount(filtro));
	}

	public Long pesquisarAelExameApPorMateriaisCount(final String filtro) {
		return examesPatologiaFacade.pesquisarAelExameApPorMateriaisCount((String) filtro);
	}

	@Override
	public List<VAelApXPatologiaAghu> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<VAelApXPatologiaAghu> result = null;
		try {
			result = examesPatologiaFacade.pesquisarVAelApXPatologiaAghu( firstResult, maxResult, orderProperty, asc, 
					residenteResp, DOMINIO_SITUACAO_EXAME_PATOLOGIA, dtDe, dtAte, 
					patologistaResp, exame, numeroAp, 
					medicoSolic, material, convenio, laudoImpresso);
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return result;
	}

	@Override
	public Long recuperarCount() {
		vAelApXPatologiaAghuCount = examesPatologiaFacade.pesquisarVAelApXPatologiaAghuCount(residenteResp, DOMINIO_SITUACAO_EXAME_PATOLOGIA, dtDe, dtAte, patologistaResp, exame, numeroAp,
				medicoSolic, material, convenio, laudoImpresso);
		return vAelApXPatologiaAghuCount.longValue();
	}

	/**
	 * Pesquisa por chave única composta caso ambos os campos que a compoem, exame e número AP, estejam preenchidos.
	 */
	public void pesquisarRegistroUnico() {
		if (isConsultaExata()) {
			this.dataModel.reiniciarPaginator();
		}
	}

	public boolean isConsultaExata() {
		return exame != null && numeroAp != null;
	}

	public AelPatologista getPatologistaResp() {
		return patologistaResp;
	}

	public void setPatologistaResp(AelPatologista patologistaResp) {
		this.patologistaResp = patologistaResp;
	}

	public AelPatologista getResidenteResp() {
		return residenteResp;
	}

	public void setResidenteResp(AelPatologista residenteResp) {
		this.residenteResp = residenteResp;
	}

	public Date getDtAte() {
		return dtAte;
	}

	public void setDtAte(Date dtAte) {
		this.dtAte = dtAte;
	}

	public AelConfigExLaudoUnico getExame() {
		return exame;
	}

	public void setExame(AelConfigExLaudoUnico exame) {
		this.exame = exame;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Date getDtDe() {
		return dtDe;
	}

	public void setDtDe(Date dtDe) {
		this.dtDe = dtDe;
	}

	public MedicoSolicitanteVO getMedicoSolic() {
		return medicoSolic;
	}

	public void setMedicoSolic(MedicoSolicitanteVO medicoSolic) {
		this.medicoSolic = medicoSolic;
	}

	public AelExameAp getMaterial() {
		return material;
	}

	public void setMaterial(AelExameAp material) {
		this.material = material;
	}

	public DominioSimNao getLaudoImpresso() {
		return laudoImpresso;
	}

	public void setLaudoImpresso(DominioSimNao laudoImpresso) {
		this.laudoImpresso = laudoImpresso;
	}

	public DominioConvenioExameSituacao getConvenio() {
		return convenio;
	}

	public void setConvenio(DominioConvenioExameSituacao convenio) {
		this.convenio = convenio;
	}

	public String getSiglaConselhoProfissional() {
		return siglaConselhoProfissional;
	}

	public void setSiglaConselhoProfissional(String siglaConselhoProfissional) {
		this.siglaConselhoProfissional = siglaConselhoProfissional;
	}

	public Integer getvAelApXPatologiaAghuCount() {
		return vAelApXPatologiaAghuCount;
	}

	public void setvAelApXPatologiaAghuCount(Integer vAelApXPatologiaAghuCount) {
		this.vAelApXPatologiaAghuCount = vAelApXPatologiaAghuCount;
	}

	public String getNroApSelecionado() {
		return nroApSelecionado;
	}

	public void setNroApSelecionado(String nroApSelecionado) {
		this.nroApSelecionado = nroApSelecionado;
	}

	public DynamicDataModel<VAelApXPatologiaAghu> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAelApXPatologiaAghu> dataModel) {
		this.dataModel = dataModel;
	}
}