package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcpProcedimentoCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioCaraterCirurgia;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoesId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcDescricoesId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;


public class DescricaoCirurgicaCirRealizadaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = -7933867498851738285L;
	
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	

	
	
	private MbcTipoAnestesias tipoAnestesia;
	
	private DominioCaraterCirurgia dominioCaraterCirurgia;
	
	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	private MbcpProcedimentoCirurgicoVO procedCirurVO;
	
	private MbcProcedimentoCirurgicos procedCirurgico;

	private boolean encaminhaMatExame;
	
	private String complemento;

	private List<MbcProcDescricoes> procDescricoes;
	
	private MbcCirurgias cirurgia;
	
	private String material;
	
	private Integer dcgCrgSeqExc;
	private Short dcgSeqpExc;
	private Integer seqpExc;
	private Integer atdSeq;
	
	private List<MbcpProcedimentoCirurgicoVO> listaProcedimentos;
	
	private MbcAnestesiaDescricoes anestesiaDescricao;
	
	private MbcDescricaoItens descricaoItem;

	private boolean encaminhamentoSolicitacao;
	
	private final String PAGE_EXAMES_SOLIC_EXAMES ="exames-solicitacaoExameCRUD";
	private final String PAGE_DESCRICAO_CIRURGICA ="blococirurgico-descricaoCirurgica";
	private final String CAMPO_OBRIGATORIO ="CAMPO_OBRIGATORIO";
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	private MbcProcDescricoes procDescricaoSelecionado;
	
	private String complementoModal;
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	@Inject
	private	DescricaoCirurgicaController descricaoCirurgicaController;
	
	@Inject
	private	DescricaoCirurgicaAvalPreSedacaoController descricaoCirurgicaAvalPreSedacaoController;
	
	private final Integer TAB_2=2; 
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO){
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		
		cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoCirurgicaVO.getDcgCrgSeq(),
				new Enum[] {MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO}, new Enum[] {MbcCirurgias.Fields.ATENDIMENTO});
		
		anestesiaDescricao = blocoCirurgicoFacade.buscarAnestesiaDescricoes(descricaoCirurgicaVO.getDcgCrgSeq(),
																		    descricaoCirurgicaVO.getDcgSeqp());
		
		if(anestesiaDescricao != null){
			tipoAnestesia = anestesiaDescricao.getTipoAnestesia();
		}
		
		descricaoItem = blocoCirurgicoFacade.buscarDescricaoItens( descricaoCirurgicaVO.getDcgCrgSeq(),
			    												   descricaoCirurgicaVO.getDcgSeqp());

		dominioCaraterCirurgia = descricaoItem.getCarater();
		
		
		final Integer atdSeq = cirurgia.getAtendimento() != null ? cirurgia.getAtendimento().getSeq() : null;
		
		encaminhamentoSolicitacao = blocoCirurgicoFacade.habilitaEncaminhamentoExame(cirurgia.getOrigemPacienteCirurgia(), cirurgia.getPaciente().getCodigo(), atdSeq);
		
		if(encaminhamentoSolicitacao){
			material = blocoCirurgicoFacade.buscaDescricaoMaterialExame(cirurgia.getOrigemPacienteCirurgia(), cirurgia.getPaciente().getCodigo(), atdSeq);
			encaminhaMatExame = material != null;
			encaminhamentoSolicitacao = (material == null);
		}
		
		if(cirurgia!=null && (cirurgia.getDataEntradaSala()== null && (cirurgia.getDataSaidaSala()!= null  || (cirurgia.getSituacao() != null && cirurgia.getSituacao().name().equals(DominioSituacaoCirurgia.PREP.name()) || cirurgia.getSituacao().name().equals(DominioSituacaoCirurgia.RZDA.name()))))){
			cirurgia.setDataEntradaSala(descricaoCirurgicaVO.getDataCirurgia());
		}
		
		if(descricaoCirurgicaVO != null) {
		listaProcedimentos = blocoCirurgicoCadastroApoioFacade.obterCursorMbcpProcedimentoCirurgicoVO( descricaoCirurgicaVO.getDcgCrgSeq(), 
				 DominioIndRespProc.AGND, 
				 DominioSituacao.A, 
				 DominioSituacao.A, 
				 DominioTipoAtuacao.RESP, 
				 descricaoCirurgicaVO.getUnidadeFuncional().getSeq());
		}
		
		inicializaListaProcedimentos();
	}
	
	public void atualizarTipoAnestesia(){

		try {
			
			anestesiaDescricao = blocoCirurgicoFacade.buscarAnestesiaDescricoes(descricaoCirurgicaVO.getDcgCrgSeq(),
																			    descricaoCirurgicaVO.getDcgSeqp());
			if(anestesiaDescricao == null){
				anestesiaDescricao = new MbcAnestesiaDescricoes();
				anestesiaDescricao.setId(new MbcAnestesiaDescricoesId( descricaoCirurgicaVO.getDcgCrgSeq(), 
																	   descricaoCirurgicaVO.getDcgSeqp(),
																	   tipoAnestesia.getSeq()
																	   )
										);
				
				anestesiaDescricao.setTipoAnestesia(tipoAnestesia);
				blocoCirurgicoFacade.inserirAnestesiaDescricoes(anestesiaDescricao);				
				//this.apresentarMsgNegocio(Severity.INFO,"ANESTESIA_DESCRICAO_INCLUSAO_SUCESSO", tipoAnestesia.getDescricao());
				
			} else {
				if(this.tipoAnestesia == null){
					this.blocoCirurgicoFacade.exluirMbcAnestesiaDescricoes(this.anestesiaDescricao);					
					//this.apresentarMsgNegocio(Severity.INFO,"ANESTESIA_DESCRICAO_REMOCAO_SUCESSO", anestesiaDescricao.getTipoAnestesia().getDescricao());

				} else{
					blocoCirurgicoFacade.alterarAnestesiaDescricoes(anestesiaDescricao, tipoAnestesia);					
					//this.apresentarMsgNegocio(Severity.INFO,"ANESTESIA_DESCRICAO_ALTERACAO_SUCESSO", tipoAnestesia.getDescricao());
				}	
			}
			descricaoCirurgicaAvalPreSedacaoController.verificarAnestesia();
			relatorioDescricaoCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} 
	}
	
	public void atualizarDominioCaraterCirurgia(){
		try {
			descricaoItem = blocoCirurgicoFacade.buscarDescricaoItens( descricaoCirurgicaVO.getDcgCrgSeq(),
					   descricaoCirurgicaVO.getDcgSeqp());
			descricaoItem.setCarater(dominioCaraterCirurgia);
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItem);
			
			//this.apresentarMsgNegocio(Severity.INFO,"CARATER_DESCRICAO_ITEM_ALTERADO_SUCESSO", dominioCaraterCirurgia.getDescricao());
			relatorioDescricaoCirurgiaController.inicio();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizarProcedimentoCirurgico(){
		procedCirurgico = null;
		
		if(procedCirurVO != null){
			procedCirurgico = blocoCirurgicoFacade.obterMbcProcedimentoCirurgicosPorId(procedCirurVO.getSeq());
		}
		this.salvarAtoCirurgico();
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	private void salvarAtoCirurgico(){
		try {
			
			if(procedCirurgico == null){
				this.apresentarMsgNegocio(Severity.ERROR,CAMPO_OBRIGATORIO, "Procedimento");
				return;
			}
			
			if(procedCirurgico.getIndContaminacao() == null){
				this.apresentarMsgNegocio(Severity.ERROR,CAMPO_OBRIGATORIO, "Contaminação");
				return;
			}
			
			final MbcProcDescricoes procDescricao = new MbcProcDescricoes();
			procDescricao.setId(new MbcProcDescricoesId( descricaoCirurgicaVO.getDcgCrgSeq(),
														 descricaoCirurgicaVO.getDcgSeqp(),
														 null 
														)
								);
			
			blocoCirurgicoFacade.validarContaminacaoProcedimentoCirurgico(procedCirurgico.getSeq(), procedCirurgico.getIndContaminacao());
			
			procDescricao.setComplemento(complemento);
			procDescricao.setIndContaminacao(procedCirurgico.getIndContaminacao());
			procDescricao.setProcedimentoCirurgico(procedCirurgico);
			
						
			blocoCirurgicoFacade.inserirMbcProcDescricoes(procDescricao);			
			//this.apresentarMsgNegocio(Severity.INFO,"PROCEDIMENTO_CIRURGICO_INCLUSAO_SUCESSO", procedCirurgico.getDescricao());
		
			inicializaListaProcedimentos();
			complemento = null;
			procedCirurgico = null;
			procedCirurVO = null;
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void atualizarAtoCirurgico(MbcProcDescricoes procDescricao){
		try {
			
			if(procDescricao.getProcedimentoCirurgico() == null){
				this.apresentarMsgNegocio(Severity.ERROR,CAMPO_OBRIGATORIO, "Procedimento");
				return;
			}
			
			if(procDescricao.getIndContaminacao() == null){
				this.apresentarMsgNegocio(Severity.ERROR,CAMPO_OBRIGATORIO, "Contaminação");
				return;
			}
			
			
			blocoCirurgicoFacade.validarContaminacaoProcedimentoCirurgico(procDescricao.getProcedimentoCirurgico().getSeq(), procDescricao.getIndContaminacao());
			
			//procDescricao.setComplemento(complemento);
			//procDescricao.setIndContaminacao(indContaminacao);
		//	procDescricao.setProcedimentoCirurgico(procedCirurgico);
			
						
			blocoCirurgicoFacade.alterarMbcProcDescricoes(procDescricao);			
			//this.apresentarMsgNegocio(Severity.INFO,"PROCEDIMENTO_CIRURGICO_INCLUSAO_SUCESSO", procedCirurgico.getDescricao());
		
			inicializaListaProcedimentos();
			complemento = null;
			procedCirurgico = null;
			procedCirurVO = null;
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void atualizarComplemento(MbcProcDescricoes procDescricao){
		try {
			blocoCirurgicoFacade.alterarMbcProcDescricoes(procDescricao);			
			inicializaListaProcedimentos();
			complemento = null;
			procedCirurgico = null;
			procedCirurVO = null;
			this.procDescricaoSelecionado = procDescricao;
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void atualizarComplementoPelaModal(){
		try {
			blocoCirurgicoFacade.alterarMbcProcDescricoes(procDescricaoSelecionado);			
			inicializaListaProcedimentos();
			complemento = null;
			procedCirurgico = null;
			procedCirurVO = null;
			relatorioDescricaoCirurgiaController.inicio();
			descricaoCirurgicaController.setAbaSelecionada(TAB_2);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public boolean validaDataInicioMenorDataSala(final Date dataInicio){
		if(DateUtil.validaDataMaiorIgual(dataInicio,cirurgia.getDataEntradaSala())){
			return false;
		}
		return true;
	}
	

	public void atualizarDataInicioCirurgia(){
		try {
			
			if(!validaAtualizacaoData(descricaoItem.getDthrInicioCirg(),true)){
				return;
			}
			
			//Incidente - AGHU #53879
			//Cirurgias/ Descrição Cirúrgica- Ao inserir um horário com o dobro permitido, é apresentado duas mensagens com o aviso
			Short tempoMinimo = null;
			inicializaListaProcedimentos();
			
			if(!procDescricoes.isEmpty()){
				final MbcProcDescricoes procDescricao = procDescricoes.get(0);
				tempoMinimo = procDescricao.getProcedimentoCirurgico() != null ? procDescricao.getProcedimentoCirurgico().getTempoMinimo() : null;	
			}
			
		//boolean estourouTempoMimCirur = 
				blocoCirurgicoFacade
								.atualizarDatasDescricaoCirurgica( descricaoCirurgicaVO.getDcgCrgSeq(), 
																   descricaoCirurgicaVO.getDcgSeqp(), 
																   cirurgia.getData(), 
																   descricaoItem.getDthrInicioCirg(), 
																   descricaoItem.getDthrFimCirg(), 
																   cirurgia.getDataEntradaSala(), 
																   cirurgia.getDataSaidaSala(), 
																   cirurgia.getUnidadeFuncional() != null ? cirurgia.getUnidadeFuncional().getSeq() : null, 
																   cirurgia.getSalaCirurgica() != null ? cirurgia.getSalaCirurgica().getId().getSeqp() : null, 
																   true, 
																   tempoMinimo);
			
/*			if(estourouTempoMimCirur){
				this.apresentarMsgNegocio(Severity.WARN,"MBC_01096");
			}*/
			
			//this.apresentarMsgNegocio(Severity.INFO,"MESSAGE_ATUALIZACAO_DATAS_CIRURGIA_SUCESSO","Início Cirurgia");
			
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void atualizarDataFimCirurgia(){
		try {
			if(!validaAtualizacaoData(descricaoItem.getDthrFimCirg(), false)){
				return;
			}
			
			//Incidente - AGHU #53879
			//Cirurgias/ Descrição Cirúrgica- Ao inserir um horário com o dobro permitido, é apresentado duas mensagens com o aviso
	
			final MbcProcDescricoes procDescricao = blocoCirurgicoFacade.buscarProcDescricoes( descricaoCirurgicaVO.getDcgCrgSeq(), 
																							   descricaoCirurgicaVO.getDcgSeqp());

			//boolean estourouTempoMimCirur = 
			blocoCirurgicoFacade.atualizarDatasDescricaoCirurgica( descricaoCirurgicaVO.getDcgCrgSeq(), 
																   descricaoCirurgicaVO.getDcgSeqp(), 
																   cirurgia.getData(), 
																   descricaoItem.getDthrInicioCirg(), 
																   descricaoItem.getDthrFimCirg(), 
																   cirurgia.getDataEntradaSala(), 
																   cirurgia.getDataSaidaSala(), 
																   cirurgia.getUnidadeFuncional() != null ? cirurgia.getUnidadeFuncional().getSeq() : null, 
																   cirurgia.getSalaCirurgica() != null ? cirurgia.getSalaCirurgica().getId().getSeqp() : null, 
																   false, 
																   procDescricao != null ? 
																   			procDescricao.getProcedimentoCirurgico() != null ? procDescricao.getProcedimentoCirurgico().getTempoMinimo() : null
																   						 : null);
			
/*			if(estourouTempoMimCirur){
				this.apresentarMsgNegocio(Severity.ERROR,"MBC_01096");
			}*/
			
			//this.apresentarMsgNegocio(Severity.INFO,"MESSAGE_ATUALIZACAO_DATAS_CIRURGIA_SUCESSO","Fim Cirurgia");
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private boolean validaAtualizacaoData(final Date data, final boolean isInicio) {
		if(data == null){
			return false;
		}
		
		if(DateValidator.validaDataMaiorQueAtual(data)){
			if (isInicio){
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_INICIO_CIRURGIA_MAIOR_DATA_ATUAL");
				descricaoItem.setDthrInicioCirg(null);
			}else {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_FIM_CIRURGIA_MAIOR_DATA_ATUAL");
				descricaoItem.setDthrFimCirg(null);
			}
			return false;
		}
		
		final MbcDescricaoItens oldMdi = blocoCirurgicoFacade.obterMbcDescricaoItensOriginal(
									 new MbcDescricaoItensId(descricaoCirurgicaVO.getDcgCrgSeq(), 
															 descricaoCirurgicaVO.getDcgSeqp()));
		if(isInicio){
			// Caso não houve alteração de datas, retorna
			if(CoreUtil.igual(descricaoItem.getDthrInicioCirg(), oldMdi.getDthrInicioCirg())){
				blocoCirurgicoFacade.desatacharMbcDescricaoItensOriginal(oldMdi);
				return false;
			}
			//Limpa data de fim que pode estar invalida ou seja maior que a data de fim da sala 
			if(!CoreUtil.igual(descricaoItem.getDthrFimCirg(), oldMdi.getDthrFimCirg())){
				this.descricaoItem.setDthrFimCirg(oldMdi.getDthrFimCirg());
			}
			
		} else {
			// Caso não houve alteração de datas, retorna
			if(CoreUtil.igual(descricaoItem.getDthrFimCirg(), oldMdi.getDthrFimCirg())){
				blocoCirurgicoFacade.desatacharMbcDescricaoItensOriginal(oldMdi);
				return false;
			}
			
			//Limpa data de inicio que pode estar invalida ou seja menor que a data de inicio da sala 
			if(!CoreUtil.igual(descricaoItem.getDthrInicioCirg(), oldMdi.getDthrInicioCirg())){
				this.descricaoItem.setDthrInicioCirg(oldMdi.getDthrInicioCirg());
			}

		}
		
		return true;
	}
	
	
	private void inicializaListaProcedimentos() {
		procDescricoes = blocoCirurgicoFacade.obterProcDescricoes( descricaoCirurgicaVO.getDcgCrgSeq(),
																   descricaoCirurgicaVO.getDcgSeqp(),
																   MbcProcDescricoes.Fields.SEQP.toString()
																 );
	}
	
	
	public void excluirAtoCirurgico(){
		try {
			final MbcProcDescricoes procDescricao = blocoCirurgicoFacade.obterMbcProcDescricoesPorChavePrimaria(
																new MbcProcDescricoesId(dcgCrgSeqExc,dcgSeqpExc,seqpExc));
		
			//final String descricao = procDescricao.getProcedimentoCirurgico().getDescricao();
			
			
			blocoCirurgicoFacade.excluirMbcProcDescricoes(procDescricao);			
			//this.apresentarMsgNegocio(Severity.INFO,"PROCEDIMENTO_CIRURGICO_REMOCAO_SUCESSO", descricao);

			inicializaListaProcedimentos();
			relatorioDescricaoCirurgiaController.inicio();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<MbcTipoAnestesias> getListaTipoAnestesia(){
		return blocoCirurgicoCadastroApoioFacade.obterMbcTipoAnestesiasPorSituacao(DominioSituacao.A);
	}

	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgicos(String filtro){
		return blocoCirurgicoCadastroApoioFacade.pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao( (String)filtro, 
																											DominioSituacao.A, 
																											MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString());
	}
	
	public void changeProcedimentoCirurgicos(){
		this.salvarAtoCirurgico();
		relatorioDescricaoCirurgiaController.inicio();
	}

	public String atualizarEncaminhamentoExame(){
		
		if(cirurgia.getAtendimento() != null){
			atdSeq =  cirurgia.getAtendimento().getSeq();
			
		} else {
			atdSeq = blocoCirurgicoFacade.obterSeqAghAtendimentoPorPaciente(cirurgia.getPaciente().getCodigo());
		}
		
		if (atdSeq == null) {
			encaminhaMatExame = false;
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_NAO_ESTA_EM_ATENDIMENTO");
			
		} else {
			solicitacaoExameController.setAtendimentoSeq(atdSeq);
			solicitacaoExameController.setPaginaChamadora(PAGE_DESCRICAO_CIRURGICA);
			
			FacesContext.getCurrentInstance().getApplication(). getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null , PAGE_EXAMES_SOLIC_EXAMES);
		}
		relatorioDescricaoCirurgiaController.inicio();
		
		return null;
	}
	
	public DominioIndContaminacao[] getDominioIndContaminacao() {
		return DominioIndContaminacao.values();
	}
	
	public List<MbcpProcedimentoCirurgicoVO> getListaProcedimentos() {
		return listaProcedimentos;
	}

	public void setListaProcedimentos(
			List<MbcpProcedimentoCirurgicoVO> listaProcedimentos) {
		this.listaProcedimentos = listaProcedimentos;
	}

	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}


	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public MbcTipoAnestesias getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(MbcTipoAnestesias tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public DominioCaraterCirurgia getDominioCaraterCirurgia() {
		return dominioCaraterCirurgia;
	}

	public void setDominioCaraterCirurgia(
			DominioCaraterCirurgia dominioCaraterCirurgia) {
		this.dominioCaraterCirurgia = dominioCaraterCirurgia;
	}

	public MbcpProcedimentoCirurgicoVO getProcedCirurVO() {
		return procedCirurVO;
	}

	public void setProcedCirurVO(MbcpProcedimentoCirurgicoVO procedCirurVO) {
		this.procedCirurVO = procedCirurVO;
	}

	public MbcProcedimentoCirurgicos getProcedCirurgico() {
		return procedCirurgico;
	}

	public void setProcedCirurgico(MbcProcedimentoCirurgicos procedCirurgico) {
		this.procedCirurgico = procedCirurgico;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public List<MbcProcDescricoes> getProcDescricoes() {
		return procDescricoes;
	}

	public void setProcDescricoes(List<MbcProcDescricoes> procDescricoes) {
		this.procDescricoes = procDescricoes;
	}

	public Integer getDcgCrgSeqExc() {
		return dcgCrgSeqExc;
	}

	public void setDcgCrgSeqExc(Integer dcgCrgSeqExc) {
		this.dcgCrgSeqExc = dcgCrgSeqExc;
	}

	public Short getDcgSeqpExc() {
		return dcgSeqpExc;
	}

	public void setDcgSeqpExc(Short dcgSeqpExc) {
		this.dcgSeqpExc = dcgSeqpExc;
	}

	public Integer getSeqpExc() {
		return seqpExc;
	}

	public void setSeqpExc(Integer seqpExc) {
		this.seqpExc = seqpExc;
	}

	public boolean isEncaminhaMatExame() {
		return encaminhaMatExame;
	}

	public void setEncaminhaMatExame(boolean encaminhaMatExame) {
		this.encaminhaMatExame = encaminhaMatExame;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public MbcAnestesiaDescricoes getAnestesiaDescricao() {
		return anestesiaDescricao;
	}

	public void setAnestesiaDescricao(MbcAnestesiaDescricoes anestesiaDescricao) {
		this.anestesiaDescricao = anestesiaDescricao;
	}

	public MbcDescricaoItens getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(MbcDescricaoItens descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public boolean isEncaminhamentoSolicitacao() {
		return encaminhamentoSolicitacao;
	}

	public void setEncaminhamentoSolicitacao(boolean encaminhamentoSolicitacao) {
		this.encaminhamentoSolicitacao = encaminhamentoSolicitacao;
	}

	public MbcProcDescricoes getProcDescricaoSelecionado() {
		return procDescricaoSelecionado;
	}

	public void setProcDescricaoSelecionado(
			MbcProcDescricoes procDescricaoSelecionado) {
		this.procDescricaoSelecionado = procDescricaoSelecionado;
	}

	public String getComplementoModal() {
		return complementoModal;
	}

	public void setComplementoModal(String complementoModal) {
		this.complementoModal = complementoModal;
	}

	
}
