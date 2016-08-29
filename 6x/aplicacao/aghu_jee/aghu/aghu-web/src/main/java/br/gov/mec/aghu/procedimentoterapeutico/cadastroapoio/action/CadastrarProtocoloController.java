package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.procedimentoterapeutico.action.CadastroSolucaoProtocoloController;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.NovaVersaoProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloSessaoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastrarProtocoloController extends ActionController  {

	private static final long serialVersionUID = 749484173808403753L;

	private static final String PAGE_PESQUISAR_PROTOCOLO = "procedimentoterapeutico-pesquisarProtocolo";
	private static final String PAGE_INCLUIR_MEDICAMENTO = "procedimentoterapeutico-cadastraMedicamento";
	private static final String PAGE_CADASTRAR_CUIDADOS_PROTOCOLO = "procedimentoterapeutico-cadastrarCuidadosProtocolo";
	private static final String PAGE_SOLUCAO = "procedimentoterapeutico-cadastrarSolucao";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private PesquisaProtocoloPaginatorController pesquisaProtocoloPaginatorController;
			
	private ProtocoloSessaoVO versaoProtocoloSessao;
	private String descrProtocolo;
	private Integer qtdCiclo;
	private String situacao;
	private DominioSituacaoProtocolo situacaoProtocolo;
	private Integer versao;
	private MptTipoSessao itemSelecionado;
	private List<MptTipoSessao> listaMptTipoSessao; 
	private List<DominioSituacaoProtocolo> situacoes;
	private Integer seqProtocolo;
	private ProtocoloVO parametroSelecionado;
	private MptProtocoloSessao mptProtocoloSessao;
	private Boolean novo = false;
	private Integer seqVersaoProtocoloSessao;
	private Boolean protocolo = false;
	private List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO = new ArrayList<ProtocoloMedicamentoSolucaoCuidadoVO>();
	private ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionadoAux;
	private List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes;
	private List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados;
	private Short diasTratamento;
	private Boolean isDiaModificado = Boolean.FALSE;
	
	private ProtocoloMedicamentoSolucaoCuidadoVO parametroModalSolucao = new ProtocoloMedicamentoSolucaoCuidadoVO();
	private List<ProtocoloItensMedicamentoVO> listaMedicamentos = new ArrayList<ProtocoloItensMedicamentoVO>();
	private Long seqProtocoloExclusaoSolucao;
	private String tituloModalSolucao = StringUtils.EMPTY;
	private Integer tamanhoLista = 0;
	private MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia;
	private String telaAnterior;
	private MptVersaoProtocoloSessao mptVersaoProtocoloSessaoPersistido;
	private boolean isPersistido = false;
	
	private boolean habilitarVersaoCopiarProtocolo;
	
	//#44287
	private boolean novaVersao = false;
	private NovaVersaoProtocoloVO protocoloNovaVersao;
	
//	private Boolean apresentaMensagemSolucao = Boolean.FALSE;
		
	@Inject
	private CadastrarMedicamentoController cadastrarMedicamentoController;

	@Inject
	private CadastroSolucaoProtocoloController cadastroSolucaoProtocoloController;
		
	private Boolean modoVisualizacao = Boolean.FALSE;
	
	@Inject
	private CadastrarCuidadosProtocoloController cadastrarCuidadosProtocoloController;
	
	private Integer seqCuidadoExclusao;
	private Boolean copiarProtocolo = false;
	
	private Boolean exibeLupa = Boolean.FALSE;
	

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio(){
		
//		if(apresentaMensagemSolucao){
//			this.apresentarMsgNegocio(Severity.INFO, "MS08_MPT_PROT_SOL_INSERIDO_SUCESSO");
//			apresentaMensagemSolucao = Boolean.FALSE;
//		}
		
		if(this.novo){
			this.parametroSelecionado = null;
			diasTratamento = 0;
		}
		if(this.parametroSelecionado != null || isPersistido){
			if(novaVersao && !isPersistido){
				this.protocoloNovaVersao = procedimentoTerapeuticoFacade.carregarItensVersaoProtocoloPorVpsSeq(this.parametroSelecionado.getSeqVersaoProtocoloSessao());
				if(protocoloNovaVersao != null){
					Integer versao = procedimentoTerapeuticoFacade.carregarNumeroUltimaVersaoPorProtocoloSeq(this.protocoloNovaVersao.getSeqProtocolo())+1;
					
					this.itemSelecionado = new MptTipoSessao();
					this.itemSelecionado.setSeq(this.protocoloNovaVersao.getTipoSessaoSeq());
					this.setSeqProtocolo(this.protocoloNovaVersao.getSeqProtocolo());
					this.setQtdCiclo(this.protocoloNovaVersao.getQtdCiclos());
					this.setDescrProtocolo(this.protocoloNovaVersao.getTitulo());
					this.setSeqVersaoProtocoloSessao(this.parametroSelecionado.getSeqVersaoProtocoloSessao());
					this.setVersao(versao);
					this.setSituacaoProtocolo(DominioSituacaoProtocolo.C);
					
					if (this.parametroSelecionado.getDiasTratamento() != null) {
						this.diasTratamento = this.parametroSelecionado.getDiasTratamento();
					} else {
						this.diasTratamento = 0;
					}
					pesquisarListaTratamento();
					verificaSituacaoProtocolo();
					try {
						gravar();
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
			}else{
				if(!isPersistido){
					this.mptProtocoloSessao  = this.procedimentoTerapeuticoFacade.obterMptProtocoloSessaoPorSeq(this.parametroSelecionado.getSeqProtocoloSessao());
					this.setSeqVersaoProtocoloSessao(this.parametroSelecionado.getSeqVersaoProtocoloSessao());
					this.setVersao(this.parametroSelecionado.getVersao());
					this.setSituacaoProtocolo(this.parametroSelecionado.getIndSituacaoVersaoProtocoloSessao());
					
					if (this.parametroSelecionado.getDiasTratamento() != null) {
						this.diasTratamento = this.parametroSelecionado.getDiasTratamento();
					} else {
						this.diasTratamento = 0;
					}
				}else{
					this.mptProtocoloSessao  = this.procedimentoTerapeuticoFacade.obterMptProtocoloSessaoPorSeq(mptVersaoProtocoloSessaoPersistido.getProtocoloSessao().getSeq());
					this.setSeqVersaoProtocoloSessao(this.mptVersaoProtocoloSessaoPersistido.getSeq());
					this.setVersao(this.mptVersaoProtocoloSessaoPersistido.getVersao());
					this.setSituacaoProtocolo(this.mptVersaoProtocoloSessaoPersistido.getIndSituacao());
					
					if (this.mptVersaoProtocoloSessaoPersistido.getDiasTratamento() != null) {
						this.diasTratamento = this.mptVersaoProtocoloSessaoPersistido.getDiasTratamento();
					} else {
						this.diasTratamento = 0;
					}
				}
				
				this.setItemSelecionado(this.mptProtocoloSessao.getTipoSessao());
				this.setSeqProtocolo(this.mptProtocoloSessao.getSeq());
				this.setQtdCiclo(this.mptProtocoloSessao.getQtdCiclo());
				this.setDescrProtocolo(this.mptProtocoloSessao.getTitulo());
			}
			
			if (!novaVersao) {
				pesquisarListaTratamento();
				verificaSituacaoProtocolo();
			}
		}else{
			if (copiarProtocolo){
				if (seqVersaoProtocoloSessao != null){
					this.versaoProtocoloSessao  = this.procedimentoTerapeuticoFacade.obterItemVersaoProtocolo(this.seqVersaoProtocoloSessao);
					itemSelecionado = new MptTipoSessao();
					if (versaoProtocoloSessao != null){
						this.itemSelecionado.setSeq(versaoProtocoloSessao.getSeqTipoSessao());
					}
				}
				if (versaoProtocoloSessao != null) {
					this.setQtdCiclo(versaoProtocoloSessao.getQtdCiclo());
					
					if (this.versaoProtocoloSessao.getDiasTratamento() != null) {
						this.diasTratamento = this.versaoProtocoloSessao.getDiasTratamento();
					} else {
						this.diasTratamento = 0;
					}
				}
				this.setSeqProtocolo(null);
				this.setDescrProtocolo(null);
				this.setVersao(1);			
				this.setSituacaoProtocolo(DominioSituacaoProtocolo.C);
				pesquisarListaTratamento();									
			}else{
				this.setVersao(1);			
				this.setSituacaoProtocolo(DominioSituacaoProtocolo.C);
			}
		}
	}
	
	public List<MptTipoSessao> listarTipoSessao(){
		this.listaMptTipoSessao =  procedimentoTerapeuticoFacade.listarMptTiposSessao();
		return this.listaMptTipoSessao;
	}

	public String cancelar() {
		this.limpar();
		if(telaAnterior != null && !telaAnterior.isEmpty()){
			return telaAnterior;
		}		
		return PAGE_PESQUISAR_PROTOCOLO;
	}

	public String editarSolucao(ProtocoloMedicamentoSolucaoCuidadoVO tratamento) {
		if(isPersistido){
			cadastroSolucaoProtocoloController.setCodVersaoProtocoloSessao(mptVersaoProtocoloSessaoPersistido.getSeq());
		}else{
			cadastroSolucaoProtocoloController.setCodVersaoProtocoloSessao(parametroSelecionado.getSeqVersaoProtocoloSessao());
		}

		List<MptProtocoloMedicamentosDia> lista = null;
		cadastroSolucaoProtocoloController.setReadOnly(false);
		if (tratamento != null) {
			lista = procedimentoTerapeuticoFacade.obterProtocoloMdtoDiaModificado(tratamento.getPtmSeq());
		} else {
			cadastroSolucaoProtocoloController.setOrdemProtocolo(getTamanhoLista());
		}

		cadastroSolucaoProtocoloController.setIsDiaModificado(Boolean.FALSE);
		
		if(tratamento != null && lista != null && lista.size() > 0) {
			cadastroSolucaoProtocoloController.setDiaMarcado(Boolean.TRUE);
			return PAGE_SOLUCAO;
		} else {
			cadastroSolucaoProtocoloController.setDiaMarcado(Boolean.FALSE);
			return PAGE_SOLUCAO;
		}
	}
	
	public void limpar(){
		this.situacaoProtocolo = null;
		this.itemSelecionado = null;
		this.listaMptTipoSessao = null;
		this.descrProtocolo = null;
		this.qtdCiclo = null;
		this.seqProtocolo = null;
		this.protocolo = false;
		this.seqVersaoProtocoloSessao = null;
		this.parametroSelecionado = null;
		this.copiarProtocolo = false;
		this.modoVisualizacao = Boolean.FALSE;
		this.parametroSelecionado = null;
		this.protocoloNovaVersao = null;
		if(novaVersao){
			this.novaVersao = false;
		}
		if(copiarProtocolo){
			this.copiarProtocolo = false;
		}
		habilitarVersaoCopiarProtocolo = false;
		this.isPersistido = false;
		exibeLupa = Boolean.FALSE;
	}
	
	public String cadastrarCuidados(){		
		cadastrarCuidadosProtocoloController.setSeqVersaoProtocoloSessaoCadastro(this.seqVersaoProtocoloSessao);		
		cadastrarCuidadosProtocoloController.setEdicao(false);
		cadastrarCuidadosProtocoloController.setEdicaoCheck(false);
		cadastrarCuidadosProtocoloController.setReadOnly(false);
		cadastrarCuidadosProtocoloController.setHabilitarSuggestion(false);
		return PAGE_CADASTRAR_CUIDADOS_PROTOCOLO;
	}
	
	public String gravar() throws ApplicationBusinessException{
		String retorno = null;
		
		if (this.validarFiltro()) {
			return retorno;
		}
		
		if(this.seqProtocolo != null){
			if(parametroSelecionado != null && !parametroSelecionado.getTituloProtocoloSessao().equals(this.descrProtocolo)){
				if(!verificaProtocoloDescricao()){
					this.procedimentoTerapeuticoFacade.atualizarProtocolo(this.itemSelecionado, this.descrProtocolo, this.qtdCiclo, this.versao, this.seqProtocolo, this.situacaoProtocolo, this.seqVersaoProtocoloSessao);
					this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_ATUALIZADO");
					retorno = cancelar();
				}
			}else{
				if(!novaVersao || isPersistido){
					this.procedimentoTerapeuticoFacade.atualizarProtocolo(this.itemSelecionado, this.descrProtocolo, this.qtdCiclo, this.versao, this.seqProtocolo, this.situacaoProtocolo, this.seqVersaoProtocoloSessao);
					this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_ATUALIZADO");
					retorno = cancelar();
				}else{
					//Carregar listas
					this.listaProtocoloMedicamentosVO = this.procedimentoTerapeuticoFacade.pesquisarListaTratamento(this.seqVersaoProtocoloSessao);		
					this.listaProtocoloSolucoes = this.procedimentoTerapeuticoFacade.pesquisarSolucoesPorVersaoProtocolo(this.seqVersaoProtocoloSessao);		 
					this.listaProtocoloCuidados = this.procedimentoTerapeuticoFacade.consultaCuidadosPorVersaoProtocolo(this.seqVersaoProtocoloSessao);
					//Persistir Nova versão 
					mptVersaoProtocoloSessaoPersistido = this.procedimentoTerapeuticoFacade.inserirNovaVersaoProtocoloSessao(this.seqProtocolo, this.versao, this.qtdCiclo, this.situacaoProtocolo, listaProtocoloMedicamentosVO, listaProtocoloSolucoes, listaProtocoloCuidados, this.parametroSelecionado.getDiasTratamento());
					this.apresentarMsgNegocio(Severity.INFO, "MSG_VERSAO_PROTOCOLO_GRAVADO");
					habilitarVersaoCopiarProtocolo = false;
					isPersistido = true;
					novaVersao = false;
					retorno = null;
					inicio();
				}
			}
		}else{
			if(!verificaProtocoloDescricao()){
				try{
					if (copiarProtocolo){
						 this.listaProtocoloMedicamentosVO = this.procedimentoTerapeuticoFacade.pesquisarListaTratamento(this.seqVersaoProtocoloSessao);		
						 this.listaProtocoloSolucoes = this.procedimentoTerapeuticoFacade.pesquisarSolucoesPorVersaoProtocolo(this.seqVersaoProtocoloSessao);		 
						 this.listaProtocoloCuidados = this.procedimentoTerapeuticoFacade.consultaCuidadosPorVersaoProtocolo(this.seqVersaoProtocoloSessao);
						 
						 mptVersaoProtocoloSessaoPersistido = this.procedimentoTerapeuticoFacade.inserirCopiaProtocolo(this.itemSelecionado, this.descrProtocolo, this.qtdCiclo, this.versao, listaProtocoloMedicamentosVO, listaProtocoloSolucoes, listaProtocoloCuidados, this.diasTratamento);
						habilitarVersaoCopiarProtocolo = false;
						this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_SALVO");
						isPersistido = true;
						copiarProtocolo = false;
						retorno = null;
						inicio();
					}else{
						this.procedimentoTerapeuticoFacade.inserirProtocolo(this.itemSelecionado, this.descrProtocolo, this.qtdCiclo, this.versao, this.diasTratamento);						
						this.apresentarMsgNegocio(Severity.INFO, "MSG_PROTOCOLO_SALVO");
						retorno = cancelar();
					}
					
				}catch(ApplicationBusinessException e){
					apresentarExcecaoNegocio(e);
				}
			}
		}
		this.pesquisaProtocoloPaginatorController.setGravarOk(true);
		return retorno;
	}
	
	public List<DominioSituacaoProtocolo> verificaSituacaoProtocolo(){
		 return this.procedimentoTerapeuticoFacade.verificarSituacaoProtocolo(this.seqVersaoProtocoloSessao);
	}
	
	public Boolean obterSituacaoProtocoloSelecionado(){
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = null;
		this.protocolo = false;
		if(this.seqVersaoProtocoloSessao != null){
			mptVersaoProtocoloSessao = this.procedimentoTerapeuticoFacade.obterSituacaoProtocolo(this.seqVersaoProtocoloSessao);
			if(mptVersaoProtocoloSessao != null && mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.C
					&& mptVersaoProtocoloSessao.getVersao() == 1){
				this.protocolo = true;
			}
		}
		return protocolo;
	}
	
	/**
	 * Valida filtros.
	 * @return Boolean.TRUE se achou inválido;
	 */
	private boolean validarFiltro() {
		boolean achouInvalido = false;
		if (this.itemSelecionado == null) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_PESQUISA_PROTOCOLO_TIPO_SESSAO_OBRIGATORIO");
			achouInvalido = true;
		}		
		if (StringUtils.isBlank(this.descrProtocolo)) {
			apresentarMsgNegocio(Severity.ERROR, "MPT_PROT_SESSAO_OBRIG_TITULO");
			achouInvalido = true;
		}	
		if (this.situacaoProtocolo == null) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_PESQUISA_PROTOCOLO_SITUACAO_OBRIGATORIO");
			achouInvalido = true;
		}
		return achouInvalido;
	}
	
	private Boolean verificaProtocoloDescricao(){
		Boolean descricaoProtocolo = false;
		MptProtocoloSessao mptProtocolo = this.procedimentoTerapeuticoFacade.verificaProtocoloPorDescricao(this.descrProtocolo);
		if(mptProtocolo != null){
			descricaoProtocolo = true;
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_PROTOCOLO_EXISTE");
		}
		return descricaoProtocolo;
	}
	
	public String pagIncluiMedicamento(){
		cadastrarMedicamentoController.setVpsSeq(this.seqVersaoProtocoloSessao);
		cadastrarMedicamentoController.setReadOnly(false);
		return PAGE_INCLUIR_MEDICAMENTO;
	}

	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarListaTratamento(){
		 this.listaProtocoloMedicamentosVO = this.procedimentoTerapeuticoFacade.pesquisarListaTratamento(this.seqVersaoProtocoloSessao);		
		 this.listaProtocoloSolucoes = this.procedimentoTerapeuticoFacade.pesquisarSolucoesPorVersaoProtocolo(this.seqVersaoProtocoloSessao);		 
		 this.listaProtocoloCuidados = this.procedimentoTerapeuticoFacade.consultaCuidadosPorVersaoProtocolo(this.seqVersaoProtocoloSessao);
		 
		 if (this.listaProtocoloSolucoes != null && !this.listaProtocoloSolucoes.isEmpty()) {
			 listaProtocoloMedicamentosVO.addAll(this.listaProtocoloSolucoes);
		 }
		 if (this.listaProtocoloCuidados != null && !this.listaProtocoloCuidados.isEmpty()) {
			 listaProtocoloMedicamentosVO.addAll(this.listaProtocoloCuidados);
		 }
		 
		 if (this.listaProtocoloMedicamentosVO != null && !this.listaProtocoloMedicamentosVO.isEmpty()) {
			 ordenarLista(this.listaProtocoloMedicamentosVO);
		 }
		 
		 return this.listaProtocoloMedicamentosVO;
	}
	
	public void excluirTratamento() throws ApplicationBusinessException{
		try {
			if(this.parametroSelecionadoAux != null){
				this.procedimentoTerapeuticoFacade.excluirTratamento(this.listaProtocoloMedicamentosVO, this.parametroSelecionadoAux);
				pesquisarListaTratamento();
				this.apresentarMsgNegocio(Severity.INFO, "MSG_TRATAMENTO_EXLUIDO");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void ordenarLista(List<ProtocoloMedicamentoSolucaoCuidadoVO> lista) {
		Collections.sort(lista, new Comparator<ProtocoloMedicamentoSolucaoCuidadoVO>() {
			public int compare(ProtocoloMedicamentoSolucaoCuidadoVO o1, ProtocoloMedicamentoSolucaoCuidadoVO o2) {
				if (o1.getOrdem() == null) {
					return -1;
				}
				if (o2.getOrdem() == null) {
					return 1;
				}
		    	if (o1.getOrdem() != null && o2.getOrdem() != null) {
		          Short ordem1 = o1.getOrdem();
		          Short ordem2 = o2.getOrdem();

		          return ordem1.compareTo(ordem2);
		       } 
		       return 0;    
		    }
		});		
	}
	
	public String buscarTooltipSolucao(ProtocoloMedicamentoSolucaoCuidadoVO tratamento){
		StringBuilder hint = new StringBuilder(250);
		
		if(tratamento.getQtdHorasCorrer() != null && tratamento.getUnidadeHorasCorrer() != null){
			hint.append("Correr em " + tratamento.getQtdHorasCorrer() + " " + tratamento.getUnidadeHorasCorrer() + "; ");
		}
		if(tratamento.getGotejo() != null && tratamento.getTvaSeq() != null){
			hint.append("Velocidade de infusão " + tratamento.getGotejo() + " " + tratamento.getDescricaoVeloc() + "; ");
		}
		if(tratamento.getIndSeNecessario()){
			hint.append("Se necessário; ");
		}
		if(tratamento.getIndUsoDomiciliar()){
			hint.append("Uso domiciliar");
			if(tratamento.getDiasUsoDomiciliar() != null){
				hint.append(" por " + tratamento.getDiasUsoDomiciliar() + " dias; ");
			} else{
				hint.append("; ");
			}
		}
		if(tratamento.getIndBombaInfusao()){
			hint.append("BI; ");
		}
		if(tratamento.getObservacao() != null){
			hint.append(tratamento.getObservacao());
		}
		return hint.toString();
	}
	
	public void carregarModalSolucao(ProtocoloMedicamentoSolucaoCuidadoVO tratamento){
		parametroModalSolucao = tratamento;
		listaMedicamentos = procedimentoTerapeuticoFacade.listarItensMedicamentoProtocolo(tratamento.getPtmSeq());
		openDialog("modalSolucaoWG");
	}
	
	public void excluirSolucao(){
		Short ordem = this.procedimentoTerapeuticoFacade.obterMedicamentoPorChavePrimaria(seqProtocoloExclusaoSolucao).getOrdem();
		procedimentoTerapeuticoFacade.excluirProtocoloItemMedicamentoDaSolucao(seqProtocoloExclusaoSolucao);
		this.procedimentoTerapeuticoFacade.atualizarCampoOrdem(this.listaProtocoloMedicamentosVO, ordem);
		pesquisarListaTratamento();
		apresentarMsgNegocio(Severity.INFO, "MS09_MPT_PROT_SOL_EXCLUIDO_SUCESSO");
	}
	
	public String editarTratamento(){
		String retorno = null;
		if (parametroSelecionadoAux != null && parametroSelecionadoAux.getPtmSeq() != null && !parametroSelecionadoAux.getIndSolucao()) {
			cadastrarMedicamentoController.setParametroSelecionado(parametroSelecionadoAux);
			cadastrarMedicamentoController.setDia(null);
			cadastrarMedicamentoController.setVpsSeq(parametroSelecionadoAux.getVpsSeq());
			retorno = PAGE_INCLUIR_MEDICAMENTO;
		}
		cadastrarMedicamentoController.setReadOnly(false);
		return retorno;
		
	}
	
	public void moverParaCima() {
		
		if (parametroSelecionadoAux != null && parametroSelecionadoAux.getOrdem() != null && parametroSelecionadoAux.getOrdem() > 1) {
			ProtocoloMedicamentoSolucaoCuidadoVO anterior = listaProtocoloMedicamentosVO.get(parametroSelecionadoAux.getOrdem() - 2);
			procedimentoTerapeuticoFacade.moverOrdemCima(parametroSelecionadoAux, anterior);
			pesquisarListaTratamento();
		}
		
	}
	
	public void moverParaBaixo() {
		
		if (parametroSelecionadoAux != null && parametroSelecionadoAux.getOrdem() != null && parametroSelecionadoAux.getOrdem() < listaProtocoloMedicamentosVO.size()) {
			ProtocoloMedicamentoSolucaoCuidadoVO posterior = listaProtocoloMedicamentosVO.get(parametroSelecionadoAux.getOrdem());
			procedimentoTerapeuticoFacade.moverOrdemBaixo(parametroSelecionadoAux, posterior);
			pesquisarListaTratamento();
		}
				
	}
		
	public void prepararExclusaoCuidados(Integer seqCuidado){
		this.seqCuidadoExclusao = seqCuidado;
		RequestContext.getCurrentInstance().execute("PF('modal_excluir_cuidado').show()");
		
	}
		
	public void fazerCheckCelula(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia) {
		procedimentoTerapeuticoFacade.fazerCheckCelula(vo, dia);		
	}
	
	public void excluirCuidados(){
		Short ordem = this.procedimentoTerapeuticoFacade.obterCuidadoParaEdicao(this.seqCuidadoExclusao).getOrdem();
		this.procedimentoTerapeuticoFacade.excluirCuidados(this.seqCuidadoExclusao);
		this.procedimentoTerapeuticoFacade.atualizarCampoOrdem(this.listaProtocoloMedicamentosVO, ordem);
		this.inicio();
		apresentarMsgNegocio(Severity.INFO, "CUIDADO_EXCLUIDO_SUCESSO");
	}
	
	public String editarCuidados(Integer seqEdicao){
		List<MptProtocoloCuidadosDia> diasCuidados = this.procedimentoTerapeuticoFacade.verificarDiaCuidado(seqEdicao);
		if(diasCuidados != null && diasCuidados.size() > 0){
			cadastrarCuidadosProtocoloController.setHabilitarSuggestion(true);
			cadastrarCuidadosProtocoloController.setDiasCuidados(diasCuidados);
		}else{
			cadastrarCuidadosProtocoloController.setHabilitarSuggestion(false);			
		}
		cadastrarCuidadosProtocoloController.setMptProtocoloCuidados(this.procedimentoTerapeuticoFacade.obterCuidadoParaEdicao(seqEdicao));
		cadastrarCuidadosProtocoloController.setEdicao(true);
		cadastrarCuidadosProtocoloController.setEdicaoCheck(false);
		cadastrarCuidadosProtocoloController.setReadOnly(false);
		return PAGE_CADASTRAR_CUIDADOS_PROTOCOLO;
	}
	
	public Boolean renderizarCheck(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia) {
		vo.setRenderizaAzul(Boolean.FALSE);
		if (vo.getPtmSeq() != null) {			
			MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia = procedimentoTerapeuticoFacade.obterProtocoloMedicamentosDiaPorPtmSeqDia(vo.getPtmSeq(), dia);
			if (mptProtocoloMedicamentosDia != null && !mptProtocoloMedicamentosDia.getIndUsoDomiciliar()) {
				vo.setStyleCelulaDia("silk-checked silk-icon");
				if (mptProtocoloMedicamentosDia.getModificado()) {
					vo.setStyleCelulaDia("silk-checked-blue silk-icon");
					vo.setRenderizaAzul(Boolean.TRUE);
					return false;
				}
				return true;
			} else if (mptProtocoloMedicamentosDia != null && mptProtocoloMedicamentosDia.getIndUsoDomiciliar()) {
				vo.setStyleCelulaDia("medicamento-domiciliar medicamento-domiciliar-enabled silk-icon");
				if (mptProtocoloMedicamentosDia.getModificado()) {
					vo.setStyleCelulaDia("medicamento-domiciliar medicamento-domiciliar-disabled silk-icon");
					vo.setRenderizaAzul(Boolean.TRUE);
					return false;
				}
				return true;
			}			
		} else if (vo.getPcuSeq() != null) {			
			MptProtocoloCuidadosDia mptProtocoloCuidadosDia = procedimentoTerapeuticoFacade.obterProtocoloCuidadosDiaPorPcuSeqDia(vo.getPcuSeq(), dia);
			if (mptProtocoloCuidadosDia != null) {
				vo.setStyleCelulaDia("silk-checked silk-icon");
				if (mptProtocoloCuidadosDia.getModificado()) {
					vo.setStyleCelulaDia("silk-checked-blue silk-icon");
					vo.setRenderizaAzul(Boolean.TRUE);
					return false;
				}
				return true;
			}		
		}
		return false;
	}
	
	public String editarTratamentoDia(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia) {
		String retorno = null;
		if (vo != null && vo.getPtmSeq() != null && !vo.getIndSolucao()) {			
			cadastrarMedicamentoController.setIsDiaModificado(isDiaModificado);
			cadastrarMedicamentoController.setDia(dia);
			MptProtocoloMedicamentosDia mdtosDia = this.procedimentoTerapeuticoFacade.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(vo.getPtmSeq(), dia);
			cadastrarMedicamentoController.setMptProtocoloMedicamentosDia(mdtosDia);
			cadastrarMedicamentoController.setVpsSeq(vo.getVpsSeq());
			cadastrarMedicamentoController.setParametroSelecionado(vo);			
			cadastrarMedicamentoController.setReadOnly(this.modoVisualizacao);
			retorno = PAGE_INCLUIR_MEDICAMENTO;
		} else if (vo != null && vo.getPtmSeq() != null && vo.getIndSolucao()){
     	   cadastroSolucaoProtocoloController.setDias(dia);
     	   cadastroSolucaoProtocoloController.setIsDiaModificado(Boolean.TRUE);
     	   cadastroSolucaoProtocoloController.setCodSolucao(vo.getPtmSeq());
     	   cadastroSolucaoProtocoloController.setCodVersaoProtocoloSessao(seqVersaoProtocoloSessao);
     	   cadastroSolucaoProtocoloController.setReadOnly(this.modoVisualizacao);
     	   retorno = PAGE_SOLUCAO;
		}		
		if(vo != null && vo.getPcuSeq() != null){
			cadastrarCuidadosProtocoloController.setDiaEdicao(this.procedimentoTerapeuticoFacade.obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(vo.getPcuSeq(), dia));
			cadastrarCuidadosProtocoloController.setHabilitarSuggestion(true);
			cadastrarCuidadosProtocoloController.setEdicaoCheck(true);
			cadastrarCuidadosProtocoloController.setEdicao(false);
			cadastrarCuidadosProtocoloController.setReadOnly(this.modoVisualizacao);
			retorno = PAGE_CADASTRAR_CUIDADOS_PROTOCOLO;
		}
		return retorno;		
	}
	
	public String obterViaGrid(ProtocoloMedicamentoSolucaoCuidadoVO vo) {
		String retorno = "";
		if (vo != null && vo.getPtmSeq() != null && vo.getIndSolucao()) {
			retorno = vo.getSiglaVia();
		} else if (vo != null && vo.getPtmSeq() != null && !vo.getIndSolucao()) {
			retorno = vo.getSiglaVia();
		}
		return retorno;
	}
	
	public String obterAprazamentoGrid(ProtocoloMedicamentoSolucaoCuidadoVO vo) {		
		String retorno = "";
		if (vo != null && vo.getPtmSeq() != null && vo.getIndSolucao()) {
			if (vo.getPtmFrequencia() != null) {
				retorno = vo.getPtmFrequencia().toString() + " " + vo.getDescricaoAprazamento();
			}else {
				retorno = vo.getDescricaoAprazamento();
			}
		} else if (vo != null && vo.getPtmSeq() != null && !vo.getIndSolucao()) {
			if (vo.getPtmFrequencia() != null) {
				retorno = vo.getPtmFrequencia().toString() + " " + vo.getDescricaoAprazamento();
			}else {
				retorno = vo.getDescricaoAprazamento();
			}
		} else if (vo.getPcuSeq() != null) {
			if (vo.getFrequenciaProtocoloCuidado() != null) {
				retorno = vo.getFrequenciaProtocoloCuidado().toString() + " " + vo.getDescricaoAprazamento();
			}else {
				retorno = vo.getDescricaoAprazamento();
			}
		}
		return retorno;
	}
	
	public String obterDescricaoTruncada(String tituloDescricao, Integer tamanho) {
		if (tituloDescricao.length() > tamanho) {
			return StringUtils.abbreviate(tituloDescricao, tamanho);
		}
		return tituloDescricao;
	}
	
	public String visualizarItens(ProtocoloMedicamentoSolucaoCuidadoVO item){
		String tela = null;
		if(item.getPcuSeq() != null){
			cadastrarCuidadosProtocoloController.setMptProtocoloCuidados(this.procedimentoTerapeuticoFacade.obterCuidadoParaEdicao(item.getPcuSeq()));
			cadastrarCuidadosProtocoloController.setReadOnly(true);
			cadastrarCuidadosProtocoloController.setEdicao(false);
			cadastrarCuidadosProtocoloController.setEdicaoCheck(false);
			tela = PAGE_CADASTRAR_CUIDADOS_PROTOCOLO;
		}else if(item.getIndSolucao() && item.getPcuSeq() == null){
			cadastroSolucaoProtocoloController.setReadOnly(true);
			cadastroSolucaoProtocoloController.setDiaMarcado(false);
			cadastroSolucaoProtocoloController.setCodSolucao(item.getPtmSeq());
			cadastroSolucaoProtocoloController.setIsEdicao(true);
			cadastroSolucaoProtocoloController.setCodVersaoProtocoloSessao(this.parametroSelecionado.getSeqVersaoProtocoloSessao());
			List<MptProtocoloMedicamentosDia> lista = null;
			if (item != null) {
				lista = procedimentoTerapeuticoFacade.obterProtocoloMdtoDiaModificado(item.getPtmSeq());
			}
			if(item != null && lista != null && lista.size() > 0) {
				cadastroSolucaoProtocoloController.setDiaMarcado(Boolean.TRUE);				
			} else {
				cadastroSolucaoProtocoloController.setDiaMarcado(Boolean.FALSE);				
			}
			tela = PAGE_SOLUCAO;
		}else if(!item.getIndSolucao() && item.getPcuSeq() == null){
			cadastrarMedicamentoController.setReadOnly(true);
			cadastrarMedicamentoController.setParametroSelecionado(item);			
			tela = PAGE_INCLUIR_MEDICAMENTO;
		}
		return tela;
	}	
	
	public void prepararExclusaoMedicamento() {
		RequestContext.getCurrentInstance().execute("PF('modal_excluir_medicamento').show()");
	}
		
	// getters & setters
	
	public Short getDiasTratamento() {
		return diasTratamento;
	}

	public void setDiasTratamento(Short diasTratamento) {
		this.diasTratamento = diasTratamento;
	}

	public MptTipoSessao getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MptTipoSessao itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public String getDescrProtocolo() {
		return descrProtocolo;
	}

	public void setDescrProtocolo(String descrProtocolo) {
		this.descrProtocolo = descrProtocolo;
	}

	public Integer getQtdCiclo() {
		return qtdCiclo;
	}

	public void setQtdCiclo(Integer qtdCiclo) {
		this.qtdCiclo = qtdCiclo;
	}

	public String getSituacao() {
		this.setSituacao("Construção");
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}

	public DominioSituacaoProtocolo getSituacaoProtocolo() {
		return situacaoProtocolo;
	}

	public void setSituacaoProtocolo(DominioSituacaoProtocolo situacaoProtocolo) {
		this.situacaoProtocolo = situacaoProtocolo;
	}

	public List<MptTipoSessao> getListaMptTipoSessao() {
		return listaMptTipoSessao;
	}

	public void setListaMptTipoSessao(List<MptTipoSessao> listaMptTipoSessao) {
		this.listaMptTipoSessao = listaMptTipoSessao;
	}

	public List<DominioSituacaoProtocolo> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<DominioSituacaoProtocolo> situacoes) {
		this.situacoes = situacoes;
	}

	public Integer getSeqProtocolo() {
		return seqProtocolo;
	}

	public void setSeqProtocolo(Integer seqProtocolo) {
		this.seqProtocolo = seqProtocolo;
	}

	public ProtocoloVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(ProtocoloVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public Boolean getNovo() {
		return novo;
	}

	public void setNovo(Boolean novo) {
		this.novo = novo;
	}

	public Integer getSeqVersaoProtocoloSessao() {
		return seqVersaoProtocoloSessao;
	}

	public void setSeqVersaoProtocoloSessao(Integer seqVersaoProtocoloSessao) {
		this.seqVersaoProtocoloSessao = seqVersaoProtocoloSessao;
	}

	public Boolean getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Boolean protocolo) {
		this.protocolo = protocolo;
	}

	public List<ProtocoloMedicamentoSolucaoCuidadoVO> getListaProtocoloMedicamentosVO() {
		return listaProtocoloMedicamentosVO;
	}

	public ProtocoloMedicamentoSolucaoCuidadoVO getParametroSelecionadoAux() {
		return parametroSelecionadoAux;
	}

	public void setParametroSelecionadoAux(
			ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionadoAux) {
		this.parametroSelecionadoAux = parametroSelecionadoAux;
	}

	public Boolean getModoVisualizacao() {
		return modoVisualizacao;
	}

	public void setModoVisualizacao(Boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}

	public Integer getSeqCuidadoExclusao() {
		return seqCuidadoExclusao;
	}

	public void setSeqCuidadoExclusao(Integer seqCuidadoExclusao) {
		this.seqCuidadoExclusao = seqCuidadoExclusao;
	}

	public ProtocoloMedicamentoSolucaoCuidadoVO getParametroModalSolucao() {
		return parametroModalSolucao;
	}

	public void setParametroModalSolucao(
			ProtocoloMedicamentoSolucaoCuidadoVO parametroModalSolucao) {
		this.parametroModalSolucao = parametroModalSolucao;
	}

	public List<ProtocoloItensMedicamentoVO> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(
			List<ProtocoloItensMedicamentoVO> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public Long getSeqProtocoloExclusaoSolucao() {
		return seqProtocoloExclusaoSolucao;
	}

	public void setSeqProtocoloExclusaoSolucao(Long seqProtocoloExclusaoSolucao) {
		this.seqProtocoloExclusaoSolucao = seqProtocoloExclusaoSolucao;
	}

	public String getTituloModalSolucao() {
		if(parametroModalSolucao != null){
			tituloModalSolucao = "Medicamentos da solução: " + parametroModalSolucao.getPtmDescricao();
		}
		return tituloModalSolucao;
	}

	public void setTituloModalSolucao(String tituloModalSolucao) {
		this.tituloModalSolucao = tituloModalSolucao;
	}

	public Integer getTamanhoLista() {
		if(this.listaProtocoloMedicamentosVO != null){
			if(this.listaProtocoloMedicamentosVO.size() >= 0){
				tamanhoLista = this.listaProtocoloMedicamentosVO.size() + 1; 
			} else if(this.listaProtocoloMedicamentosVO.size() == 0){
				tamanhoLista = 1;
			}
		}
		return tamanhoLista;
	}

	public void setTamanhoLista(Integer tamanhoLista) {
		this.tamanhoLista = tamanhoLista;
	}
	
	public MptProtocoloMedicamentosDia getMptProtocoloMedicamentosDia() {
		return mptProtocoloMedicamentosDia;
	}

	public void setMptProtocoloMedicamentosDia(
			MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia) {
		this.mptProtocoloMedicamentosDia = mptProtocoloMedicamentosDia;
	}

	public ProtocoloSessaoVO getVersaoProtocoloSessao() {
		return versaoProtocoloSessao;
	}

	public void setVersaoProtocoloSessao(ProtocoloSessaoVO versaoProtocoloSessao) {
		this.versaoProtocoloSessao = versaoProtocoloSessao;
	}

	public Boolean getCopiarProtocolo() {
		return copiarProtocolo;
	}

	public void setCopiarProtocolo(Boolean copiarProtocolo) {
		this.copiarProtocolo = copiarProtocolo;
	}

	public Boolean getIsDiaModificado() {
		return isDiaModificado;
	}

	public void setIsDiaModificado(Boolean isDiaModificado) {
		this.isDiaModificado = isDiaModificado;
	}
			
	public String getTelaAnterior() {
		return telaAnterior;
	}

	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}

	public boolean isNovaVersao() {
		return novaVersao;
	}

	public void setNovaVersao(boolean novaVersao) {
		this.novaVersao = novaVersao;
	}

	public NovaVersaoProtocoloVO getProtocoloNovaVersao() {
		return protocoloNovaVersao;
	}

	public void setProtocoloNovaVersao(NovaVersaoProtocoloVO protocoloNovaVersao) {
		this.protocoloNovaVersao = protocoloNovaVersao;
	}

//	public Boolean getApresentaMensagemSolucao() {
//		return apresentaMensagemSolucao;
//	}
//
//	public void setApresentaMensagemSolucao(Boolean apresentaMensagemSolucao) {
//		this.apresentaMensagemSolucao = apresentaMensagemSolucao;
//	}

	public PesquisaProtocoloPaginatorController getPesquisaProtocoloPaginatorController() {
		return pesquisaProtocoloPaginatorController;
	}

	public void setPesquisaProtocoloPaginatorController(
			PesquisaProtocoloPaginatorController pesquisaProtocoloPaginatorController) {
		this.pesquisaProtocoloPaginatorController = pesquisaProtocoloPaginatorController;
	}

	public boolean isHabilitarVersaoCopiarProtocolo() {
		return habilitarVersaoCopiarProtocolo;
	}

	public void setHabilitarVersaoCopiarProtocolo(
			boolean habilitarVersaoCopiarProtocolo) {
		this.habilitarVersaoCopiarProtocolo = habilitarVersaoCopiarProtocolo;
	}

	public MptVersaoProtocoloSessao getMptVersaoProtocoloSessaoPersistido() {
		return mptVersaoProtocoloSessaoPersistido;
	}

	public void setMptVersaoProtocoloSessaoPersistido(
			MptVersaoProtocoloSessao mptVersaoProtocoloSessaoPersistido) {
		this.mptVersaoProtocoloSessaoPersistido = mptVersaoProtocoloSessaoPersistido;
	}

	public boolean isPersistido() {
		return isPersistido;
	}

	public void setPersistido(boolean isPersistido) {
		this.isPersistido = isPersistido;
	}

	public Boolean getExibeLupa() {
		return exibeLupa;
	}

	public void setExibeLupa(Boolean exibeLupa) {
		this.exibeLupa = exibeLupa;
	}
		
}
