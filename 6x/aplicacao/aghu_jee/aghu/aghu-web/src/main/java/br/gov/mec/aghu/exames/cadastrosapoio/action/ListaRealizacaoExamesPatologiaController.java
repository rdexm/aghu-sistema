package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.patologia.action.LaudoUnicoController;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListaRealizacaoExamesPatologiaController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<VAelApXPatologiaAghu> dataModel;
	
	@Inject 
	private LaudoUnicoController laudoUnicoController;
	
	private static final long serialVersionUID = -7102047377830610820L;

	private static final String REDIRECT_LAUDO_UNICO = "exames-laudounico";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AelPatologista residenteResp;
	private DominioSituacaoExamePatologia situacaoExmAnd;
	private Long numeroAp;
	private String sigla;

	private AelPatologista patologistaResp;
	private AelConfigExLaudoUnico exame;

	private Date dtDe;
	private Date dtAte;
	
	private String codigoBarras;
	private VAelApXPatologiaAghu exameSelecionado;

	/**
	 * Inicializa valores defaults dos principais campos da tela
	 */
	public void iniciar() {
	 

		this.dataModel.setDefaultMaxRow(30);

		if (this.dataModel.getPesquisaAtiva()) {
			return;
		}

		if (dtDe == null && dtAte == null) {
			inicializarDatas();
		}

		if (situacaoExmAnd == null) {
			situacaoExmAnd = DominioSituacaoExamePatologia.EA;
		}

		inicializarResponsavel();

		pesquisar();
	
	}

	public void inicializarResponsavel() {
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
			final Integer periodoDias = (this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERIODO_REFERENCIA_LISTA_EXAMES_AP).getVlrNumerico().intValue() * -1);// Negativo para retroceder a data. 

			dtAte = new Date();
			dtDe = DateUtil.adicionaDias(new Date(), periodoDias);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		if (DateUtil.validaDataMenor(dtAte, dtDe)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_LISTA_REALIZACAO_EXAMES_PATOLOGIA_PERIODO_INVALIDO");
			return;
		}

		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Pesquisa por chave única composta caso ambos os campos que a compoem, exame e número AP, estejam preenchidos.
	 */
	public void pesquisarRegistroUnico() {
		if (isConsultaExata()) {
			this.dataModel.reiniciarPaginator();
		}
	}

	public void limpar() {
		residenteResp = null;
		situacaoExmAnd = null;
		numeroAp = null;

		patologistaResp = null;
		exame = null;
		codigoBarras = null;
		
		this.dataModel.limparPesquisa();
		inicializarDatas();
		inicializarResponsavel();
		situacaoExmAnd = DominioSituacaoExamePatologia.EA;
		this.dataModel.setPesquisaAtiva(true);
	}
	
	public String buscaExameCodigoBarra() {
		residenteResp = null;
		situacaoExmAnd = null;
		numeroAp = null;
		
		patologistaResp = null;
		exame = null;
		
		inicializarDatas();

		if(codigoBarras != null && !codigoBarras.isEmpty()) {
			sigla = codigoBarras.substring(0,2);
			sigla = sigla.trim();
			exame = examesPatologiaFacade.obterPorSigla(sigla);
			
			if(exame == null){
				apresentarMsgNegocio(Severity.ERROR, "MSG_CODIGO_BARRAS_INVALIDO", codigoBarras);
			}else{
				try {
					numeroAp = Long.valueOf(codigoBarras.substring(2,codigoBarras.length()));
					
					List<VAelApXPatologiaAghu> result = null;
					try {
						
						result = examesPatologiaFacade.pesquisarVAelApXPatologiaAghu(0, 30, null, true, residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp );
						if(result.size() > 0 && result.get(0) != null){
							setExameSelecionado(result.get(0));
							laudoUnicoController.setNumeroAp(getExameSelecionado().getId().getLumNumeroAp()); 
							laudoUnicoController.setConfigExameId(getExameSelecionado().getId().getLu2Seq());  
							laudoUnicoController.setVoltarPara("listaRealizacaoExamesPatologia");
							return REDIRECT_LAUDO_UNICO;
						}
						
						return null;
						
					} catch (BaseException e) {
						apresentarExcecaoNegocio(e);
					}
					
				} catch (final NumberFormatException e) {
					apresentarMsgNegocio(Severity.ERROR, "MSG_CODIGO_BARRAS_INVALIDO", codigoBarras);
				}
			}
		}
		
		return null;
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

	@Override
	public List<VAelApXPatologiaAghu> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<VAelApXPatologiaAghu> result = null;
		try {
			
			result = examesPatologiaFacade.pesquisarVAelApXPatologiaAghu(firstResult, maxResult, orderProperty, asc,
																			residenteResp, situacaoExmAnd, dtDe, dtAte, 
																			patologistaResp, exame, numeroAp );
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return result;
	}

	@Override
	public Long recuperarCount() {
		return examesPatologiaFacade.pesquisarVAelApXPatologiaAghuCount(residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp).longValue();
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

	public DominioSituacaoExamePatologia getSituacaoExmAnd() {
		return situacaoExmAnd;
	}

	public void setSituacaoExmAnd(DominioSituacaoExamePatologia situacaoExmAnd) {
		this.situacaoExmAnd = situacaoExmAnd;
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

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}	

	public DynamicDataModel<VAelApXPatologiaAghu> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAelApXPatologiaAghu> dataModel) {
		this.dataModel = dataModel;
	}

	public VAelApXPatologiaAghu getExameSelecionado() {
		return exameSelecionado;
	}

	public void setExameSelecionado(VAelApXPatologiaAghu exameSelecionado) {
		this.exameSelecionado = exameSelecionado;
	}

}