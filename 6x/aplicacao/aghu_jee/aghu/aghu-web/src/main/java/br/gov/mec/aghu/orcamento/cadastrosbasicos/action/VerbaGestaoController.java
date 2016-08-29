package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioPrioridadeFonteRecurso;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class VerbaGestaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -980478715885329719L;

	private static final String VERBA_GESTAO_LIST = "verbaGestaoList";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	private FsoVerbaGestao verbaGestao;
	private Integer verbaSeq;
	private boolean visualizar;
	private String descricao;
	private DominioSituacao situacao;
	private String planoInterno;
	private DominioSimNao convenio;
	private String numeroInterno;
	private BigInteger numeroSiafiSiconv;
	private Short exercicio;
	private Date vigenciaInicialVG;
	private Date vigenciaFinalVG;
	private FsoFontesRecursoFinanc fonteRecurso;
	private DominioPrioridadeFonteRecurso prioridade;
	private Date vigenciaInicialFR;
	private Date vigenciaFinalFR;
	private List<FsoFontesXVerbaGestao> listaFonteRecurso;
	private List<FsoFontesXVerbaGestao> ListaFonteRecursoRetirarRelacao;
	private boolean emAlteracao;
	private boolean inclusao;
	private boolean mostraModalAdvertencia;
	private boolean advertenciaNumeroInterno = false;
	private boolean advertenciaSiafiSiconv = false;
	private boolean advertenciaPlano = false;
	private FsoFontesXVerbaGestao fonteRecursoExcluida;
	
	// indica alteracoes pendentes
	private boolean confirmarVoltar;
	private boolean mostraModalPendencias;
	private boolean mostraAdvertenciaExclusao;
	private boolean iniciouTela;
	

	public String iniciar() {
	 

		String retorno = null;
		
		if(!iniciouTela){
			emAlteracao = false;
			iniciouTela = true;
			ListaFonteRecursoRetirarRelacao = new ArrayList<FsoFontesXVerbaGestao>();
			
			if (verbaSeq == null) {
				verbaGestao = new FsoVerbaGestao();
				listaFonteRecurso = new ArrayList<FsoFontesXVerbaGestao>();
				setSituacao(DominioSituacao.A);
				inclusao = true;
			} else {
				if (verbaGestao == null){
				   retorno = buscaVerbaFontesRecurso(verbaSeq);
				}
				inclusao = false;
				confirmarVoltar = false;
			}
		}
		
		return retorno;
	
	}
	
	private String buscaVerbaFontesRecurso(Integer verbaSeq){
		verbaGestao = cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(verbaSeq);

		if (verbaGestao != null) {
			this.setDescricao(verbaGestao.getDescricao());
			this.setSituacao(verbaGestao.getSituacao());
			this.setPlanoInterno(verbaGestao.getIndDetPi());
			
			if(verbaGestao.getIndConvEspecial()) {
				setConvenio(DominioSimNao.S);
			} else {
				setConvenio(DominioSimNao.N);
			}
			
			this.setNumeroInterno(verbaGestao.getNroInterno());
			this.setNumeroSiafiSiconv(verbaGestao.getNroConvSiafi());
			this.setExercicio(verbaGestao.getAnoExercicio());
			this.setVigenciaInicialVG(verbaGestao.getDtIniConv());
			this.setVigenciaFinalVG(verbaGestao.getDtFimConv());

			listaFonteRecurso = cadastrosBasicosOrcamentoFacade.pesquisarFontesXVerba(verbaGestao);
		} else {
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		return null;
	}

	public void adicionar() {
		if (fonteRecurso == null) {
			this.apresentarMsgNegocio(Severity.INFO, "TITLE_FONTE_RECURSO");
			return;
		}

		if (prioridade == null) {
			this.apresentarMsgNegocio(Severity.INFO, "TITLE_PRIORIDADE");
			return;
		}

		if (vigenciaInicialFR == null) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DATA_INICIAL_FR_OBRIGATORIO");
			return;
		} else if (vigenciaInicialVG != null && this.vigenciaInicialFR.before(this.vigenciaInicialVG)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DATA_INICIAL_FR_ANTERIOR_VG");
			return;
		} 
		if (DominioSimNao.S.equals(convenio) && vigenciaFinalFR == null) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DATA_FINAL_FR_OBRIGATORIO");
			return;
		} else if (vigenciaFinalVG != null && this.vigenciaFinalFR.after(this.vigenciaFinalVG)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DATA_FINAL_FR_POSTERIOR_VG");
			return;
		}
		
		if (emAlteracao) {
			for (FsoFontesXVerbaGestao fonteXVerba : listaFonteRecurso) {
				if (fonteXVerba.getEmEdicao()) {
					fonteXVerba.setFonteRecursoFinanceiro(fonteRecurso);
					fonteXVerba.setIndPrioridade(prioridade);
					fonteXVerba.setDtVigIni(vigenciaInicialFR);
					fonteXVerba.setDtVigFim(vigenciaFinalFR);
					fonteXVerba.setEmEdicao(false);
					break;
				}
			}
		} else {
			FsoFontesXVerbaGestao fonteXVerba = new FsoFontesXVerbaGestao();
			fonteXVerba.setVerbaGestao(verbaGestao);
			fonteXVerba.setFonteRecursoFinanceiro(fonteRecurso);
			fonteXVerba.setIndPrioridade(prioridade);
			fonteXVerba.setDtVigIni(vigenciaInicialFR);
			fonteXVerba.setDtVigFim(vigenciaFinalFR);

			listaFonteRecurso.add(fonteXVerba);
		}

		emAlteracao = false;

		fonteRecurso = null;
		prioridade = null;
		vigenciaInicialFR = null;
		vigenciaFinalFR = null;
		
		confirmarVoltar = true;
	}
	
	public String confirmaGravacao() {	
		setMostraModalPendencias(false);
		setMostraAdvertenciaExclusao(false);
		
		verificaTamanhoCampos();

		if (isMostraModalAdvertencia()) {			
			return null;
		}
		
		return this.gravar();
	}
	

	public String gravar() {
		DominioSituacao situacaoOriginal = this.getSituacao();
		
		verbaGestao.setDescricao(this.getDescricao());
		verbaGestao.setSituacao(this.getSituacao());
		verbaGestao.setIndDetPi(this.getPlanoInterno());
		
		if(this.getConvenio().equals(DominioSimNao.S)) {
			verbaGestao.setIndConvEspecial(true);
		} else {
			verbaGestao.setIndConvEspecial(false);
		}
		
		verbaGestao.setNroInterno(this.getNumeroInterno());
		verbaGestao.setNroConvSiafi(this.getNumeroSiafiSiconv());
		verbaGestao.setAnoExercicio(this.getExercicio());
		verbaGestao.setDtIniConv(this.getVigenciaInicialVG());
		verbaGestao.setDtFimConv(this.getVigenciaFinalVG());

		try {			
			cadastrosBasicosOrcamentoFacade.gravaFontesRecursoXVerbaGestao( verbaGestao, listaFonteRecurso, ListaFonteRecursoRetirarRelacao);
			
			buscaVerbaFontesRecurso(verbaGestao.getSeq());
			
			if (isInclusao()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VERBA_GESTAO_GRAVADA_COM_SUCESSO");
			} else {
				
				if (situacaoOriginal != verbaGestao.getSituacao()){
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VERBA_SEM_FONTE_RECURSO");					
				}else{	
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VERBA_GESTAO_ALTERADA_COM_SUCESSO");
				}				
			}
			
			return voltar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String confirmaExclusao(FsoFontesXVerbaGestao fonteRecursoExcluida){
		setFonteRecursoExcluida(fonteRecursoExcluida);
		
		setMostraAdvertenciaExclusao(true);
		setConfirmarVoltar(false);
		setMostraModalAdvertencia(false);
		
		return null;
	}

	public void excluirFonteRecurso() {
		ListaFonteRecursoRetirarRelacao.add(fonteRecursoExcluida);
		listaFonteRecurso.remove(fonteRecursoExcluida);
		confirmarVoltar = true;
		setMostraAdvertenciaExclusao(false);
	}

	public void editarFonteRecurso(FsoFontesXVerbaGestao objetoEditado) {
		for (FsoFontesXVerbaGestao fonteXVerba : listaFonteRecurso) {
			if (fonteXVerba.getEmEdicao()) {
				fonteXVerba.setEmEdicao(false);
			}
		}	
		fonteRecurso = objetoEditado.getFonteRecursoFinanceiro();
		prioridade = objetoEditado.getIndPrioridade();
		vigenciaInicialFR = objetoEditado.getDtVigIni();
		vigenciaFinalFR = objetoEditado.getDtVigFim();
		
		objetoEditado.setEmEdicao(true);
		emAlteracao = true;
	}

	public void limpaFormFonteRecurso() {
		for (FsoFontesXVerbaGestao fonteXVerba : listaFonteRecurso) {
			if (fonteXVerba.getEmEdicao()) {
				fonteXVerba.setEmEdicao(false);
			}
		}	

		emAlteracao = false;
		fonteRecurso = null;
		prioridade = null;
		vigenciaInicialFR = null;
		vigenciaFinalFR = null;
	}

	public void verificaTamanhoCampos() {		
//		if (planoInterno != null && planoInterno.length() != 0) {
//			if (planoInterno.length() < 3 || planoInterno.length() > 11) {
//				advertenciaPlano = true;
//			}else{
//				advertenciaPlano = false;
//			}
//		} else {
			advertenciaPlano = false;
//		}
		
		if (advertenciaPlano){
			setMostraModalAdvertencia(true);
		}else{
			setMostraModalAdvertencia(false);
		}		
	}
	
	public String verificaPendencias(){
		setMostraModalAdvertencia(false);
		setMostraAdvertenciaExclusao(false);
		
		if (this.confirmarVoltar) {
            setMostraModalPendencias(true);
			openDialog("modalConfirmacaoPendenciaWG");
		}else{
			setMostraModalPendencias(false);
		}

		return this.voltar();
	}
	 
	public void campoComValorAlterado() {
		this.setConfirmarVoltar(true);
	}	

	public void atualizarDataInicioVigencia() {
		if (DominioSimNao.S.equals(this.convenio)){
			this.vigenciaInicialVG = new Date();
		} else {
			this.vigenciaInicialVG = null;
			this.vigenciaFinalVG = null;
			this.numeroInterno = null;
			this.exercicio = null;
			this.numeroSiafiSiconv = null;
		}
		this.campoComValorAlterado();
	}
	
	public String voltar() {
		verbaGestao = null;
		verbaSeq = null;
		visualizar = false;
		descricao = null;
		situacao = null;
		planoInterno = null;
		convenio = null;
		numeroInterno = null;
		numeroSiafiSiconv = null;
		exercicio = null;
		vigenciaInicialVG = null;
		vigenciaFinalVG = null;
		fonteRecurso = null;
		prioridade = null;
		vigenciaInicialFR = null;
		vigenciaFinalFR = null;
		listaFonteRecurso = null;
		ListaFonteRecursoRetirarRelacao = null;
		emAlteracao = false;
		inclusao = false;
		mostraModalAdvertencia = false;
		advertenciaNumeroInterno = false;
		advertenciaSiafiSiconv = false;
		advertenciaPlano = false;
		fonteRecursoExcluida = null;
		confirmarVoltar = false;
		mostraModalPendencias = false;
		mostraAdvertenciaExclusao = false;
		iniciouTela = false;
		
		return VERBA_GESTAO_LIST;
	}

	public List<FsoFontesRecursoFinanc> pesquisarFonteRecursoPorCodigoOuDescricao(String paramPesquisa) {
		return cadastrosBasicosOrcamentoFacade.pesquisarFonteRecursoPorCodigoOuDescricao(paramPesquisa);
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public Integer getVerbaSeq() {
		return verbaSeq;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public void setVerbaSeq(Integer verbaSeq) {
		this.verbaSeq = verbaSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getPlanoInterno() {
		return planoInterno;
	}

	public void setPlanoInterno(String planoInterno) {
		this.planoInterno = planoInterno;
	}

	public DominioSimNao getConvenio() {
		return convenio;
	}

	public void setConvenio(DominioSimNao convenio) {
		this.convenio = convenio;
	}

	public String getNumeroInterno() {
		return numeroInterno;
	}

	public void setNumeroInterno(String numeroInterno) {
		this.numeroInterno = numeroInterno;
	}

	public BigInteger getNumeroSiafiSiconv() {
		return numeroSiafiSiconv;
	}

	public void setNumeroSiafiSiconv(BigInteger numeroSiafiSiconv) {
		this.numeroSiafiSiconv = numeroSiafiSiconv;
	}

	public Short getExercicio() {
		return exercicio;
	}

	public void setExercicio(Short exercicio) {
		this.exercicio = exercicio;
	}

	public Date getVigenciaInicialVG() {
		return vigenciaInicialVG;
	}

	public void setVigenciaInicialVG(Date vigenciaInicialVG) {
		this.vigenciaInicialVG = vigenciaInicialVG;
	}

	public Date getVigenciaFinalVG() {
		return vigenciaFinalVG;
	}

	public void setVigenciaFinalVG(Date vigenciaFinalVG) {
		this.vigenciaFinalVG = vigenciaFinalVG;
	}

	public FsoFontesRecursoFinanc getFonteRecurso() {
		return fonteRecurso;
	}

	public void setFonteRecurso(FsoFontesRecursoFinanc fonteRecurso) {
		this.fonteRecurso = fonteRecurso;
	}

	public DominioPrioridadeFonteRecurso getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(DominioPrioridadeFonteRecurso prioridade) {
		this.prioridade = prioridade;
	}

	public Date getVigenciaInicialFR() {
		return vigenciaInicialFR;
	}

	public void setVigenciaInicialFR(Date vigenciaInicialFR) {
		this.vigenciaInicialFR = vigenciaInicialFR;
	}

	public Date getVigenciaFinalFR() {
		return vigenciaFinalFR;
	}

	public void setVigenciaFinalFR(Date vigenciaFinalFR) {
		this.vigenciaFinalFR = vigenciaFinalFR;
	}

	public List<FsoFontesXVerbaGestao> getListaFonteRecurso() {
		return listaFonteRecurso;
	}

	public void setListaFonteRecurso(
			List<FsoFontesXVerbaGestao> listaFonteRecurso) {
		this.listaFonteRecurso = listaFonteRecurso;
	}

	public List<FsoFontesXVerbaGestao> getListaFonteRecursoRetirarRelacao() {
		return ListaFonteRecursoRetirarRelacao;
	}

	public void setListaFonteRecursoRetirarRelacao(
			List<FsoFontesXVerbaGestao> listaFonteRecursoRetirarRelacao) {
		ListaFonteRecursoRetirarRelacao = listaFonteRecursoRetirarRelacao;
	}

	public boolean isEmAlteracao() {
		return emAlteracao;
	}

	public void setEmAlteracao(boolean emAlteracao) {
		this.emAlteracao = emAlteracao;
	}

	public boolean isInclusao() {
		return inclusao;
	}

	public void setInclusao(boolean inclusao) {
		this.inclusao = inclusao;
	}

	public boolean isMostraModalAdvertencia() {
		return mostraModalAdvertencia;
	}

	public void setMostraModalAdvertencia(boolean mostraModalAdvertencia) {
		this.mostraModalAdvertencia = mostraModalAdvertencia;
	}

	public ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}

	public void setCadastrosBasicosOrcamentoFacade(
			ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade) {
		this.cadastrosBasicosOrcamentoFacade = cadastrosBasicosOrcamentoFacade;
	}

	public boolean isAdvertenciaNumeroInterno() {
		return advertenciaNumeroInterno;
	}

	public void setAdvertenciaNumeroInterno(boolean advertenciaNumeroInterno) {
		this.advertenciaNumeroInterno = advertenciaNumeroInterno;
	}

	public boolean isAdvertenciaSiafiSiconv() {
		return advertenciaSiafiSiconv;
	}

	public void setAdvertenciaSiafiSiconv(boolean advertenciaSiafiSiconv) {
		this.advertenciaSiafiSiconv = advertenciaSiafiSiconv;
	}

	public boolean isAdvertenciaPlano() {
		return advertenciaPlano;
	}

	public void setAdvertenciaPlano(boolean advertenciaPlano) {
		this.advertenciaPlano = advertenciaPlano;
	}

	public boolean isConfirmarVoltar() {
		return confirmarVoltar;
	}

	public void setConfirmarVoltar(boolean confirmarVoltar) {
		this.confirmarVoltar = confirmarVoltar;
	}

	public boolean isMostraModalPendencias() {
		return mostraModalPendencias;
	}

	public void setMostraModalPendencias(boolean mostraModalPendencias) {
		this.mostraModalPendencias = mostraModalPendencias;
	}

	public boolean isMostraAdvertenciaExclusao() {
		return mostraAdvertenciaExclusao;
	}

	public void setMostraAdvertenciaExclusao(boolean mostraAdvertenciaExclusao) {
		this.mostraAdvertenciaExclusao = mostraAdvertenciaExclusao;
	}

	public FsoFontesXVerbaGestao getFonteRecursoExcluida() {
		return fonteRecursoExcluida;
	}

	public void setFonteRecursoExcluida(FsoFontesXVerbaGestao fonteRecursoExcluida) {
		this.fonteRecursoExcluida = fonteRecursoExcluida;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
}