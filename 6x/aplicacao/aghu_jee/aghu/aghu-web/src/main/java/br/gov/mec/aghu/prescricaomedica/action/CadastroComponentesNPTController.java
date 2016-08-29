package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoNpt;
import br.gov.mec.aghu.dominio.DominioTipoCaloria;
import br.gov.mec.aghu.estoque.vo.ParamComponenteVO;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaDecimalComponenteNpt;
import br.gov.mec.aghu.model.AfaDecimalComponenteNptId;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaParamComponenteNpt;
import br.gov.mec.aghu.model.AfaParamComponenteNptId;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroComponentesNPTController extends ActionController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5741054495945319762L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private static final String REDIRECIONA_PESQUISA_COMPONENTES = "pesquisaComponentesNPT";
	
	private List<DominioIdentificacaoComponenteNPT> identificacoes  = new ArrayList<DominioIdentificacaoComponenteNPT>();
	
	private List<DominioTipoCalculoNpt> tipoCalculos  = new ArrayList<DominioTipoCalculoNpt>();
	
	// FORM 1
	private VMpmMdtosDescr medicamento;
	private AfaGrupoComponenteNpt grupo;
	private String descricao;
	private Short ordem;
	private DominioIdentificacaoComponenteNPT identificacao;
	private DominioTipoCalculoNpt tipoCalculo;
	private Boolean ativoBoolean;
	private Boolean adultoBoolean;
	private Boolean pediatriaBoolean;
	private String observacao;
	private Integer componenteSeq;
	
	private boolean modoEdicao;
	
	private boolean modoEdicaoAba1;
	
	private boolean modoEdicaoAba2;
	
	private DominioTipoCaloria tipoCaloria;
	private List<DominioTipoCaloria> tipoCalorias  = new ArrayList<DominioTipoCaloria>();
	
	private List<ParamComponenteVO> paramList = new ArrayList<ParamComponenteVO>();
	
	private List<AfaDecimalComponenteNpt> casas = new ArrayList<AfaDecimalComponenteNpt>();
	
	// FORM 2
	// aba 1
	private AfaParamComponenteNpt parametros = new AfaParamComponenteNpt();
	private MpmUnidadeMedidaMedica unidade;
	private Boolean calculaVolume;
	private Boolean ativoAba1;
	private ParamComponenteVO paramRemover;
	
	// aba 2
	private AfaDecimalComponenteNpt casa = new AfaDecimalComponenteNpt();
	private boolean ativoCasa;
	private AfaDecimalComponenteNpt casaRemover;
	
	List<AfaDecimalComponenteNpt> casasList = new ArrayList<AfaDecimalComponenteNpt>();
	
	private Integer activeTab;
	
	private Boolean permissao;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		identificacoes.addAll(Arrays.asList(DominioIdentificacaoComponenteNPT.values()));
		tipoCalorias.addAll(Arrays.asList(DominioTipoCaloria.values()));
		tipoCalculos.addAll(Arrays.asList(DominioTipoCalculoNpt.values()));
		
		permissao = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterCadastrosNPT", "manter");
	}
	
	public void inicio() {
		ativoBoolean = true;
		ativoAba1 = true;
		if(componenteSeq != null){
			AfaComponenteNpt entity = prescricaoMedicaFacade.obterComponenteNptPorChave(componenteSeq);
			this.descricao = entity.getDescricao();
			this.observacao = entity.getObservacao();
			this.ordem = entity.getOrdem();
			this.ativoBoolean = false;
			if(DominioSituacao.A.equals(entity.getIndSituacao())){
				this.ativoBoolean = true;
			}
			this.adultoBoolean = entity.getIndAdulto();
			this.pediatriaBoolean = entity.getIndPediatria();
			if(entity.getGcnSeq() != null){
				grupo = prescricaoMedicaFacade.obterGrupoComponente(entity.getGcnSeq());
			}
			AfaMedicamento med = prescricaoMedicaFacade.obterMedicamento(entity.getMedMatCodigo());
			populaVoParam();
			populaCasas();
			this.identificacao = entity.getIdentifComponente();
			medicamento = prescricaoMedicaFacade.obterVMpmMdtosDescrPorMedicamento(med);
			modoEdicao = true;
		}
	}
	
	public void populaVoParam(){
		paramList = new ArrayList<ParamComponenteVO>();
		List<AfaParamComponenteNpt> listaParametros = new ArrayList<AfaParamComponenteNpt>();
		listaParametros = prescricaoMedicaFacade.listarComponentesPorMatCodigo(componenteSeq);
		for (AfaParamComponenteNpt afaParamComponenteNpt : listaParametros) {
			ParamComponenteVO item = new ParamComponenteVO();
			item.setParam(afaParamComponenteNpt);
			
			MpmUnidadeMedidaMedica unidadeMedica = prescricaoMedicaFacade.obterUnidadeMedica(item.getParam().getUmmSeq());
			
			if(unidadeMedica != null){
				item.setUmmDescricao(unidadeMedica.getDescricao());
			}
			paramList.add(item);
		}
	}
	
	public void populaCasas(){
		casasList = new ArrayList<AfaDecimalComponenteNpt>();
		casasList = prescricaoMedicaFacade.listarCasasDecimaisPorMatCodigo(componenteSeq);
	}
	
	public void gravar(){
		//if(adultoBoolean == null && pediatriaBoolean == null){
		//	apresentarMsgNegocio(Severity.ERROR, "PREENCHER_ADULTO_PEDIATRIA");
		//}else{
			if(componenteSeq != null){
				AfaComponenteNpt entity = new AfaComponenteNpt();
				entity.setDescricao(descricao);
				entity.setObservacao(observacao);
				if(identificacao != null){
					entity.setIdentifComponente(identificacao);
				}else{
					entity.setIdentifComponente(null);
				}
				entity.setOrdem(ordem);
				entity.setIndAdulto(adultoBoolean != null ? adultoBoolean : false);
				entity.setIndPediatria(pediatriaBoolean != null ? pediatriaBoolean : false);
				
				entity.setIndSituacao(DominioSituacao.I);
				if(ativoBoolean){
					entity.setIndSituacao(DominioSituacao.A);
				}
				
				try {
					prescricaoMedicaFacade.atualizarComponenteNpt(entity, medicamento.getMatCodigo(), grupo != null ? grupo.getSeq() : null);
					apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_COMPONENTE");
					modoEdicao = true;
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
				
			}else{
				inserir();
			}
		//}
	}
	
	private void inserir(){
		AfaComponenteNpt entity = new AfaComponenteNpt();
		entity.setDescricao(descricao);
		entity.setObservacao(observacao);
		if(identificacao != null){
			entity.setIdentifComponente(identificacao);
		}
		entity.setOrdem(ordem);
		entity.setIndAdulto(adultoBoolean != null ? adultoBoolean : false);
		entity.setIndPediatria(pediatriaBoolean != null ? pediatriaBoolean : false);
		
		entity.setIndSituacao(DominioSituacao.I);
		if(ativoBoolean){
			entity.setIndSituacao(DominioSituacao.A);
		}
		try {
			prescricaoMedicaFacade.gravarComponenteNpt(entity, medicamento.getMatCodigo(), grupo != null ? grupo.getSeq() : null);
			componenteSeq = medicamento.getMatCodigo();
			populaVoParam();
			populaCasas();
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_COMPONENTE");
			modoEdicao = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarParam(){
		if(parametros != null){
			if(parametros.getId() != null && parametros.getId().getSeqp() != null){
				parametros.setUmmSeq(unidade.getSeq());
				if(tipoCaloria != null){
					parametros.setTipoCaloria(tipoCaloria);
				}else{
					parametros.setTipoCaloria(null);
				}
				parametros.setIndCalculaVolume("N");
				if(calculaVolume){
					parametros.setIndCalculaVolume("S");
				}
				parametros.setIndSituacao(DominioSituacao.I);
				if(ativoAba1){
					parametros.setIndSituacao(DominioSituacao.A);
				}
				parametros.setTipoParamCalculo(tipoCalculo);

				try {
					prescricaoMedicaFacade.atualizarParamComponenteNpt(parametros);
					modoEdicaoAba1 = false;
					populaVoParam();
					apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_PARAMETRO_NPT");
					cancelarEditarParam();
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}else{
				// gravar NOVO
				AfaParamComponenteNptId id = new AfaParamComponenteNptId();
				id.setCnpMedMatCodigo(componenteSeq);
				
				parametros.setId(id);
				parametros.setUmmSeq(unidade.getSeq());
				if(tipoCaloria != null){
					parametros.setTipoCaloria(tipoCaloria);
				}
				parametros.setIndCalculaVolume("N");
				if(calculaVolume){
					parametros.setIndCalculaVolume("S");
				}
				parametros.setIndSituacao(DominioSituacao.I);
				if(ativoAba1){
					parametros.setIndSituacao(DominioSituacao.A);
				}
				parametros.setTipoParamCalculo(tipoCalculo);

				try {
					prescricaoMedicaFacade.gravarParamComponenteNpt(parametros);
					modoEdicaoAba1 = false;
					populaVoParam();
					apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_PARAMETRO_NPT");
					cancelarEditarParam();
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}
	
	public void gravarCasa(){
		if(casa != null){
			if(casa.getNroCasasDecimais() == null){
				apresentarMsgNegocio(Severity.ERROR, "MESSAGE_CASAS_DECIMAIS_OBRIGATORIO");
			}
			else if(casa.getPesoInicial() == null){
				apresentarMsgNegocio(Severity.ERROR, "MESSAGE_PESO_INCIAL_OBRIGATORIO");
			}
			else if(casa.getPesoFinal() == null){
				apresentarMsgNegocio(Severity.ERROR, "MESSAGE_PESO_FINAL_OBRIGATORIO");
			}else{
				if(casa.getId() != null && casa.getId().getSeqp() != null){
					casa.setIndSituacao(DominioSituacao.I);
					if(ativoCasa){
						casa.setIndSituacao(DominioSituacao.A);
					}
	
					try {
						prescricaoMedicaFacade.atualizarCasaComponenteNpt(casa);
						modoEdicaoAba2 = false;
						populaCasas();
						apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_CASA_NPT");
						cancelarEditarCasa();
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}else{
					// gravar NOVO
					AfaDecimalComponenteNptId id = new AfaDecimalComponenteNptId();
					id.setCnpMedMatCodigo(componenteSeq);
					
					casa.setId(id);
					
					casa.setIndSituacao(DominioSituacao.I);
					if(ativoCasa){
						casa.setIndSituacao(DominioSituacao.A);
					}
	
					try {
						prescricaoMedicaFacade.gravarCasaComponenteNpt(casa);
						modoEdicaoAba2 = false;
						populaCasas();
						apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_CASA_NPT");
						cancelarEditarCasa();
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
			}
		}
	}
	
	

	
	public void editarParam(ParamComponenteVO item){
		modoEdicaoAba1 = true;
		parametros = item.getParam();
		unidade = prescricaoMedicaFacade.obterUnidadeMedica(item.getParam().getUmmSeq());
		calculaVolume =  false;
		if("S".equals(parametros.getIndCalculaVolume())){
			calculaVolume =  true;
		}
		ativoAba1 = false;
		if(DominioSituacao.A.equals(parametros.getIndSituacao())){
			ativoAba1 = true;
		}
		tipoCalculo = parametros.getTipoParamCalculo();
		tipoCaloria = parametros.getTipoCaloria();
	}
	
	public void cancelarEditarParam(){
		modoEdicaoAba1 = false;
		parametros = new AfaParamComponenteNpt(); 
		calculaVolume = false;
		ativoAba1 = true;
		tipoCalculo = null;
		unidade = null;
		tipoCaloria = null;
	}
	
	public void cancelarEditarCasa(){
		modoEdicaoAba2 = false;
		casa = new AfaDecimalComponenteNpt(); 
		ativoCasa = true;
	}
	
	public void removerParam(){
		try {
			prescricaoMedicaFacade.removerParamComponenteNpt(paramRemover.getParam());
			populaVoParam();
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_PARAMETRO_NPT");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editarCasa(AfaDecimalComponenteNpt item){
		modoEdicaoAba2 = true;
		casa = item;

		ativoCasa = false;
		if(DominioSituacao.A.equals(item.getIndSituacao())){
			ativoCasa = true;
		}
	}
	
	public void removerCasa(){
		try {
			prescricaoMedicaFacade.removerCasaComponenteNpt(casaRemover);
			populaCasas();
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_CASA_NPT");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<VMpmMdtosDescr> obterSuggestionMed(String strPesquisa){
		return prescricaoMedicaFacade.obterSuggestionMedicamento(strPesquisa);
	}
	
	public List<AfaGrupoComponenteNpt> obterSuggestionGrupoComponentes(String strPesquisa){
		return prescricaoMedicaFacade.obterSuggestionGrupoComponentes(strPesquisa);
	}
	
	public List<MpmUnidadeMedidaMedica> obterSuggestionUnidade(String strPesquisa){
		return prescricaoMedicaFacade.obterSuggestionUnidade(strPesquisa);
	}

	
	public String voltar(){
		limparTela();
		return REDIRECIONA_PESQUISA_COMPONENTES;
	}
	
	public void limparTela(){
		// primeiro form
		componenteSeq = null;
		medicamento = null;
		grupo = null;
		descricao = null;
		ordem = null;
		identificacao = null;
		ativoBoolean = true;
		adultoBoolean = false;
		pediatriaBoolean = false;
		observacao = null;
		modoEdicao = false;
		modoEdicaoAba1 = false;
		modoEdicaoAba2 = false;
		parametros = new AfaParamComponenteNpt();
		casa = new AfaDecimalComponenteNpt();
		paramList = new ArrayList<ParamComponenteVO>();
		casasList = new ArrayList<AfaDecimalComponenteNpt>();
		ativoAba1 = true;
		ativoCasa = true;
		unidade = null;
		activeTab = 0;
	}
	
	public void onTabChange(TabChangeEvent event) {
		 String abaSelecionada = event.getTab().getId();
		 if(StringUtils.isNotBlank(abaSelecionada)) {
		 Integer indiceAbaSelecionada = Integer.valueOf(StringUtils.replace(abaSelecionada, "tab", ""));
		 activeTab = indiceAbaSelecionada;
		 }
	}
	
	public VMpmMdtosDescr getMedicamento() {
		return medicamento;
	}
	
	public void setMedicamento(VMpmMdtosDescr medicamento) {
		this.medicamento = medicamento;
	}
	
	public AfaGrupoComponenteNpt getGrupo() {
		return grupo;
	}
	
	public void setGrupo(AfaGrupoComponenteNpt grupo) {
		this.grupo = grupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public List<DominioIdentificacaoComponenteNPT> getIdentificacoes() {
		return identificacoes;
	}
	public void setIdentificacoes(List<DominioIdentificacaoComponenteNPT> identificacoes) {
		this.identificacoes = identificacoes;
	}
	public DominioIdentificacaoComponenteNPT getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(DominioIdentificacaoComponenteNPT identificacao) {
		this.identificacao = identificacao;
	}

	public Boolean getAtivoBoolean() {
		return ativoBoolean;
	}

	public void setAtivoBoolean(Boolean ativoBoolean) {
		this.ativoBoolean = ativoBoolean;
	}

	public Boolean getAdultoBoolean() {
		return adultoBoolean;
	}

	public void setAdultoBoolean(Boolean adultoBoolean) {
		this.adultoBoolean = adultoBoolean;
	}

	public Boolean getPediatriaBoolean() {
		return pediatriaBoolean;
	}

	public void setPediatriaBoolean(Boolean pediatriaBoolean) {
		this.pediatriaBoolean = pediatriaBoolean;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getComponenteSeq() {
		return componenteSeq;
	}

	public void setComponenteSeq(Integer componenteSeq) {
		this.componenteSeq = componenteSeq;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public AfaParamComponenteNpt getParametros() {
		return parametros;
	}

	public void setParametros(AfaParamComponenteNpt parametros) {
		this.parametros = parametros;
	}

	public MpmUnidadeMedidaMedica getUnidade() {
		return unidade;
	}

	public void setUnidade(MpmUnidadeMedidaMedica unidade) {
		this.unidade = unidade;
	}

	public DominioTipoCaloria getTipoCaloria() {
		return tipoCaloria;
	}

	public void setTipoCaloria(DominioTipoCaloria tipoCaloria) {
		this.tipoCaloria = tipoCaloria;
	}

	public List<DominioTipoCaloria> getTipoCalorias() {
		return tipoCalorias;
	}

	public void setTipoCalorias(List<DominioTipoCaloria> tipoCalorias) {
		this.tipoCalorias = tipoCalorias;
	}

	public List<ParamComponenteVO> getParamList() {
		return paramList;
	}

	public void setParamList(List<ParamComponenteVO> paramList) {
		this.paramList = paramList;
	}

	public List<AfaDecimalComponenteNpt> getCasas() {
		return casas;
	}

	public void setCasas(List<AfaDecimalComponenteNpt> casas) {
		this.casas = casas;
	}

	public List<DominioTipoCalculoNpt> getTipoCalculos() {
		return tipoCalculos;
	}

	public void setTipoCalculos(List<DominioTipoCalculoNpt> tipoCalculos) {
		this.tipoCalculos = tipoCalculos;
	}

	public DominioTipoCalculoNpt getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(DominioTipoCalculoNpt tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public AfaDecimalComponenteNpt getCasa() {
		return casa;
	}

	public void setCasa(AfaDecimalComponenteNpt casa) {
		this.casa = casa;
	}

	public boolean isAtivoCasa() {
		return ativoCasa;
	}

	public void setAtivoCasa(boolean ativoCasa) {
		this.ativoCasa = ativoCasa;
	}

	public List<AfaDecimalComponenteNpt> getCasasList() {
		return casasList;
	}

	public void setCasasList(List<AfaDecimalComponenteNpt> casasList) {
		this.casasList = casasList;
	}

	public boolean isModoEdicaoAba1() {
		return modoEdicaoAba1;
	}

	public void setModoEdicaoAba1(boolean modoEdicaoAba1) {
		this.modoEdicaoAba1 = modoEdicaoAba1;
	}

	public boolean isModoEdicaoAba2() {
		return modoEdicaoAba2;
	}

	public void setModoEdicaoAba2(boolean modoEdicaoAba2) {
		this.modoEdicaoAba2 = modoEdicaoAba2;
	}

	public Boolean getCalculaVolume() {
		return calculaVolume;
	}

	public void setCalculaVolume(Boolean calculaVolume) {
		this.calculaVolume = calculaVolume;
	}

	public Boolean getAtivoAba1() {
		return ativoAba1;
	}

	public void setAtivoAba1(Boolean ativoAba1) {
		this.ativoAba1 = ativoAba1;
	}

	public ParamComponenteVO getParamRemover() {
		return paramRemover;
	}

	public void setParamRemover(ParamComponenteVO paramRemover) {
		this.paramRemover = paramRemover;
	}

	public AfaDecimalComponenteNpt getCasaRemover() {
		return casaRemover;
	}

	public void setCasaRemover(AfaDecimalComponenteNpt casaRemover) {
		this.casaRemover = casaRemover;
	}
	
	 public Integer getActiveTab() {
	      return activeTab;
	 }

	 public void setActiveTab(Integer activeTab) {
	      this.activeTab = activeTab;
	 }

	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}
	
}
