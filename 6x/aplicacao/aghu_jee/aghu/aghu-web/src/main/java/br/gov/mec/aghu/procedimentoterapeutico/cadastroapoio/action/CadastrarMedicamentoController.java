package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioCalculoDose;
import br.gov.mec.aghu.dominio.DominioPadronizado;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AfaFormaDosagemVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastrarMedicamentoController extends ActionController  {


	private static final long serialVersionUID = 749484173808403753L;

	private static final String PAGE_CADASTRAR_PROTOCOLO = "procedimentoterapeutico-cadastraProtocolo";
	private static final String TRES_ESPACOS = "   ";
	private static final String BARRA_M2 = "/m2";
	private static final String BARRA_KG = "/Kg";
	private static final String LABEL_DOSE_UNITARIA = "Dose Unitária";
	private static final String LABEL_DOSE_AUC = "AUC";
	private static final DominioCalculoDose MOSTELLER = DominioCalculoDose.M;
	private static final DominioCalculoDose CALVERT = DominioCalculoDose.C;
	private static final DominioCalculoDose DUBOIS = DominioCalculoDose.D;
	private static final DominioCalculoDose FIXO = DominioCalculoDose.F;
	private static final DominioCalculoDose PESO = DominioCalculoDose.G;
	private static final DominioCalculoDose HAYCOCK = DominioCalculoDose.H;
		
	private DominioPadronizado padronizacao;
	private MptProtocoloMedicamentos mptProtocoloMedicamentos = new MptProtocoloMedicamentos();
	private MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos =  new MptProtocoloItemMedicamentos();
	private MedicamentosVO medicamentosVO;
	private List<MedicamentosVO> listaMedicamentosVO;
	private AfaFormaDosagemVO unidadeSelecionada;
	private AfaViaAdministracao viaSelecionada;
	private MpmTipoFrequenciaAprazamento aprazamentoSelecionado;
	private AfaTipoVelocAdministracoes unidadeInfusaoSelecionada;
	private Boolean todasVias;
	private Short correrEm;
	private Integer vpsSeq;
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	private List<AfaMensagemMedicamento> listaMensagemMedicamento = new ArrayList<AfaMensagemMedicamento>();
	private List<AfaFormaDosagemVO> listaFormaDosagemVO;
	private List<AfaFormaDosagemVO> listaFormaDosagemVOParamDose;
	private ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionado;
	private List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia;
	private Boolean isDiaModificado = Boolean.FALSE;
	private Short dia;
	private AfaMedicamento diluenteSelecionado;
	private Boolean medIndExigeObservacao = Boolean.FALSE;
	private Boolean mensagem  = Boolean.FALSE;
	private Boolean isFrequencia = Boolean.FALSE;
	private Short ordem;
	private Boolean readOnly;
	private ParametroDoseUnidadeVO paramDoseVO = new ParametroDoseUnidadeVO();
	private String unidadeBase;
	private Boolean habilitarAdicionarParamDose = Boolean.FALSE;
	private List<ParametroDoseUnidadeVO> listaParam = new ArrayList<ParametroDoseUnidadeVO>();
	private List<ParametroDoseUnidadeVO> listaParamExcluidos = new ArrayList<ParametroDoseUnidadeVO>();
	private Boolean isEdicaoCalculoDose = Boolean.FALSE;
	private Integer seqNegativa = -1;
	private ParametroDoseUnidadeVO paramDoseEditado = new ParametroDoseUnidadeVO();
	private String labelDoseUnitaria = LABEL_DOSE_UNITARIA;
	private ParametroDoseUnidadeVO parametroDoseSelecionado = new ParametroDoseUnidadeVO();
	private Boolean desabilitarCampoUnidade = Boolean.FALSE;
	private String barra = "/";
	private Boolean isValida = Boolean.FALSE;
	private MedicamentosVO medicamentosVOAuxiliar = new MedicamentosVO();
	private MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia;
		  
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private CadastrarProtocoloController cadastrarProtocoloController;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio(){
		 this.padronizacao = DominioPadronizado.S;
		 if(parametroSelecionado != null){
			this.mptProtocoloMedicamentos =  this.procedimentoTerapeuticoFacade.obterMedicamentoPorChavePrimaria(parametroSelecionado.getPtmSeq());
			this.mptProtocoloItemMedicamentos =  this.procedimentoTerapeuticoFacade.obterItemMedicamentoPorChavePrimaria(parametroSelecionado.getSeqItemProtocoloMdtos());
			this.medicamentosVO = this.procedimentoTerapeuticoFacade.obterMedicamento(null, padronizacao.isSim(), parametroSelecionado.getMedMatCodigo());
			this.medicamentosVOAuxiliar = medicamentosVO;
			listaParam = procedimentoTerapeuticoFacade.preCarregarListaParametroDoseMedicamento(parametroSelecionado.getMedMatCodigo(), parametroSelecionado.getSeqItemProtocoloMdtos());
			habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
			this.setViaSelecionada(this.procedimentoTerapeuticoFacade.obterVia(this.mptProtocoloMedicamentos.getVadSigla().getSigla()));
			this.setAprazamentoSelecionado(this.procedimentoTerapeuticoFacade.obterAprazamento(this.mptProtocoloMedicamentos.getTfqSeq().getSeq()));
			if(this.mptProtocoloMedicamentos.getTipoVelocAdministracoes() != null){
				this.setUnidadeInfusaoSelecionada(this.procedimentoTerapeuticoFacade.obterUnidadeInfusao(this.mptProtocoloMedicamentos.getTipoVelocAdministracoes().getSeq()));
			}
			pesquisarFormaDosagem();
			pesquisarFormaDosagemParametroDose();
			if(mptProtocoloItemMedicamentos.getAfaFormaDosagem() != null){
				this.unidadeSelecionada = this.procedimentoTerapeuticoFacade.obterFormaDosagem(mptProtocoloItemMedicamentos.getAfaFormaDosagem().getSeq());
				if(!this.listaFormaDosagemVO.isEmpty()){
					for(AfaFormaDosagemVO unidade : this.listaFormaDosagemVO){
						if(this.unidadeSelecionada.getFdsSeq().equals(unidade.getFdsSeq())){
							this.setUnidadeSelecionada(unidade);
//							unidadeSelecionadaParamDose = unidade;
							break;
						}
					}
				}
			}
			if(this.mptProtocoloMedicamentos.getUnidHorasCorrer() != null){
				this.setUnidHorasCorrer(DominioUnidadeHorasMinutos.valueOf(this.mptProtocoloMedicamentos.getUnidHorasCorrer().toString()));
			}
			pesquiarInformacaoFormacologica();
			verificarProtocoloMdtoDiaModificado();
			if(this.readOnly){
				this.isFrequencia = this.readOnly;
			}
			if(parametroSelecionado.getMedMatCodigoDiluente() != null){
				this.setDiluenteSelecionado(this.procedimentoTerapeuticoFacade.obterAfaMedicamentoPorChavePrimaria(parametroSelecionado.getMedMatCodigoDiluente()));
			}
			
			if (dia != null) {
				 mptProtocoloMedicamentos.setDescricao(mptProtocoloMedicamentosDia.getDescricao());
				 this.setAprazamentoSelecionado(this.procedimentoTerapeuticoFacade.obterAprazamento(this.mptProtocoloMedicamentosDia.getTfqSeq()));
				 this.setViaSelecionada(this.procedimentoTerapeuticoFacade.obterVia(this.mptProtocoloMedicamentosDia.getVadSigla()));
				 if(this.mptProtocoloMedicamentosDia.getTvaSeq() != null) {
					 this.setUnidadeInfusaoSelecionada(this.procedimentoTerapeuticoFacade.obterUnidadeInfusao(this.mptProtocoloMedicamentosDia.getTvaSeq()));
				 }
				 mptProtocoloMedicamentos.setIndSeNecessario(mptProtocoloMedicamentosDia.getIndSeNecessario());
				 mptProtocoloMedicamentos.setFrequencia(mptProtocoloMedicamentosDia.getFrequencia());
				 mptProtocoloMedicamentos.setQtdeHorasCorrer(mptProtocoloMedicamentosDia.getQtdHorasCorrer());
				 unidHorasCorrer = mptProtocoloMedicamentosDia.getUnidHorasCorrer();
				 mptProtocoloMedicamentos.setIndBombaInfusao(mptProtocoloMedicamentosDia.getIndBombaInfusao());
				 mptProtocoloMedicamentos.setIndInfusorPortatil(mptProtocoloMedicamentosDia.getIndInfusorPortatil());
				 mptProtocoloMedicamentos.setComplemento(mptProtocoloMedicamentosDia.getComplemento());
				 mptProtocoloItemMedicamentos.setObservacao(mptProtocoloMedicamentosDia.getObservacao());
				 mptProtocoloMedicamentos.setIndDomiciliar(mptProtocoloMedicamentosDia.getIndUsoDomiciliar());
				 mptProtocoloMedicamentos.setDiasDeUsoDomiciliar(mptProtocoloMedicamentosDia.getDiasUsoDomiciliar());
				 mptProtocoloMedicamentos.setTempo(mptProtocoloMedicamentosDia.getTempo());
				 mptProtocoloMedicamentos.setGotejo(mptProtocoloMedicamentosDia.getGotejo());
				 mptProtocoloMedicamentos.setVolumeMl(mptProtocoloMedicamentosDia.getVolumeMl());
			}
		 }
	}	
	
	public List<MedicamentosVO> pesquisarMedicamentos(String objPesquisa){
		this.listaMedicamentosVO = this.procedimentoTerapeuticoFacade.pesquisarMedicamentos(objPesquisa, padronizacao.isSim());
		return this.returnSGWithCount(this.listaMedicamentosVO, this.procedimentoTerapeuticoFacade.pesquisarMedicamentosCount(objPesquisa, padronizacao.isSim()));
	}

	public List<AfaViaAdministracao> pesquisarVia(String objPesquisa){
		if(this.todasVias == null){
			this.todasVias = false;
		}
		if(!this.mensagem){
			return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.pesquisarVia(objPesquisa, this.todasVias, this.medicamentosVO), this.procedimentoTerapeuticoFacade.pesquisarViaCount(objPesquisa, this.todasVias, this.medicamentosVO));
		}else{
			this.setMensagem(false);
			return this.procedimentoTerapeuticoFacade.pesquisarVia(objPesquisa, this.todasVias, this.medicamentosVO);
		}
	}
	
	public List<MpmTipoFrequenciaAprazamento> pesquisarFrequenciaAprazamento(String objPesquisa){
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.pesquisarFrequenciaAprazamento(objPesquisa), this.procedimentoTerapeuticoFacade.pesquisarFrequenciaAprazamentoCount(objPesquisa));
	}
	
	public List<AfaFormaDosagemVO> pesquisarFormaDosagem(){
		if(this.medicamentosVO != null && this.medicamentosVO.getMedMatCodigo() != null){
			this.listaFormaDosagemVO =  this.procedimentoTerapeuticoFacade.pesquisaFormaDosagem(this.medicamentosVO.getMedMatCodigo());
			return this.listaFormaDosagemVO;
		}else{
			return null;
		}
	}
	
	public List<AfaFormaDosagemVO> pesquisarFormaDosagemParametroDose(){
		if(this.medicamentosVO != null && this.medicamentosVO.getMedMatCodigo() != null){
			this.listaFormaDosagemVOParamDose =  this.procedimentoTerapeuticoFacade.pesquisaFormaDosagem(this.medicamentosVO.getMedMatCodigo());
			return this.listaFormaDosagemVOParamDose;
		}else{
			return null;
		}
	}
	
	public List<AfaTipoVelocAdministracoes> pesquisarUnidadeInfusao(){
		if(this.procedimentoTerapeuticoFacade.pesquisaTipoVelocAdministracoes() != null){
			return this.procedimentoTerapeuticoFacade.pesquisaTipoVelocAdministracoes();
		}else{
			return null;
		}
	}
	
	public void pesquisar(){
		String objPesquisa = null;
		if(this.medicamentosVO != null){
			this.setMensagem(true);
			this.mptProtocoloMedicamentos.setDescricao(this.medicamentosVO.getMedDescricao());
			this.setMedIndExigeObservacao(this.medicamentosVO.getMedIndExigeObservacao());
			pesquisarFormaDosagem();
			pesquisarVia(objPesquisa);
			pesquiarInformacaoFormacologica();
			pesquisarFormaDosagemParametroDose();
			if(this.medicamentosVO.getMedUmmSeq() != null){
				if(!this.listaFormaDosagemVO.isEmpty()){
					for(AfaFormaDosagemVO unidade : this.listaFormaDosagemVO){
						if(this.medicamentosVO.getMedUmmSeq().equals(unidade.getFdoUmmSeq())){
							this.setUnidadeSelecionada(unidade);
							paramDoseVO.setAfaFormaDosagemVO(unidade);
							break;
						}
					}
				}
			}
			pesquisarDiluente();
		}else{
			if(listaParam.isEmpty()) {
				limparCamposEspecificos();
			} else {
				openDialog("modalExcluirDose");
			}
		}
	}
	
	public void limparListaParamDose(){
		if(listaParam != null && !listaParam.isEmpty()){
			for(ParametroDoseUnidadeVO objParam : listaParam){
				if(objParam.getSeq() > 0){
//					procedimentoTerapeuticoFacade.excluirParametroDoseMedicamento(objParam.getSeq());
					listaParamExcluidos.add(objParam);
				}
			}
		}
		this.listaParam = new ArrayList<ParametroDoseUnidadeVO>();
		this.listaFormaDosagemVOParamDose = new ArrayList<AfaFormaDosagemVO>();
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
		limparCamposEspecificos();
	}
	
	public void preencherSuggestionboxMedicamentoPosAlteracao(){
		if(medicamentosVO == null){
			medicamentosVO = medicamentosVOAuxiliar;
		}
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
	}
		
	public String pesquiarInformacaoFormacologica(){
		//String val = "\n";
		StringBuilder result = new StringBuilder();
		if(this.medicamentosVO != null && this.medicamentosVO.getMedMatCodigo() != null){
			List<String> retornoList = this.procedimentoTerapeuticoFacade.pesquisaInformacaoFormacologicaAux(this.medicamentosVO.getMedMatCodigo());
			if(!retornoList.isEmpty()){
				for(String item : retornoList)
				{
					result.append(item);
					/*if(item.contains(".")){
						result.append(val);
					}*/
				}
			}
			return result.toString();
		}else{
			return null;
		}
	}
	
	public void obterTempo() throws ParseException{
		String tempo = null;
		Integer hora = null;
		Integer minuto = null;
		String data = "00/00/0000";
		Date tempoFormatado = null;
			
			if(this.mptProtocoloMedicamentos.getQtdeHorasCorrer() != null && this.mptProtocoloMedicamentos.getQtdeHorasCorrer() > 0 && this.unidHorasCorrer!= null){
				this.mptProtocoloMedicamentos.setUnidHorasCorrer(DominioUnidadeHorasMinutos.valueOf(this.unidHorasCorrer.toString()));
				if(this.mptProtocoloMedicamentos.getUnidHorasCorrer().equals(DominioUnidadeHorasMinutos.H)){
					if(this.mptProtocoloMedicamentos.getQtdeHorasCorrer() >= 0 && this.mptProtocoloMedicamentos.getQtdeHorasCorrer() <= 9){
						tempo = "0".concat(String.valueOf(this.mptProtocoloMedicamentos.getQtdeHorasCorrer())).concat(":").concat("0").concat("0");
						}else {
							if(this.mptProtocoloMedicamentos.getQtdeHorasCorrer() > 23){
								tempo = "23:59";
							}else{
								tempo = this.mptProtocoloMedicamentos.getQtdeHorasCorrer().toString().concat(":").concat("0").concat("0");	
							}
						}
				}else{
					
					hora = this.mptProtocoloMedicamentos.getQtdeHorasCorrer() / 60;
					minuto = this.mptProtocoloMedicamentos.getQtdeHorasCorrer() % 60;
					tempo = hora.toString().concat(":").concat(minuto.toString());
							
				}
				
				tempo = data.concat(" ").concat(tempo);
				DateFormat horario = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			    tempoFormatado = (Date) horario.parse(tempo);
						
			    this.mptProtocoloMedicamentos.setTempo(tempoFormatado);
			}else{
				this.mptProtocoloMedicamentos.setTempo(null);
			}
		}
		
	public String  voltar(){
		limpar();
		return PAGE_CADASTRAR_PROTOCOLO;
	}
	
	public void limpar(){
		limparCamposEspecificos();
		this.mptProtocoloItemMedicamentos = new MptProtocoloItemMedicamentos();
		this.mptProtocoloMedicamentos = new MptProtocoloMedicamentos();
		this.listaParam = new ArrayList<ParametroDoseUnidadeVO>();
		this.listaParamExcluidos = new ArrayList<ParametroDoseUnidadeVO>();
		this.paramDoseVO = new ParametroDoseUnidadeVO();
		this.correrEm = null;
		this.unidHorasCorrer = null;
		this.aprazamentoSelecionado = null;
		this.todasVias = null;
		this.unidadeInfusaoSelecionada = null;
		this.parametroSelecionado = null;
		this.isDiaModificado = false;
		this.medIndExigeObservacao = false;
		this.isFrequencia = false;
		this.ordem = null;
		this.habilitarAdicionarParamDose = false;
		this.labelDoseUnitaria = LABEL_DOSE_UNITARIA;
		this.listaFormaDosagemVOParamDose = new ArrayList<AfaFormaDosagemVO>();
		this.listaFormaDosagemVO = new ArrayList<AfaFormaDosagemVO>();
		this.isValida = false;
	}
	
	public void limparCamposEspecificos(){
		this.medicamentosVO = null;
		this.medicamentosVOAuxiliar = null;
		this.mptProtocoloMedicamentos.setDescricao(null); 
		this.unidadeSelecionada = null;
		this.viaSelecionada = null;
		this.diluenteSelecionado = null;
		this.listaFormaDosagemVOParamDose = new ArrayList<AfaFormaDosagemVO>();
	}
	
	public void gravar() throws ApplicationBusinessException{
		try {
			if(this.mptProtocoloMedicamentos.getFrequencia() != null && this.mptProtocoloMedicamentos.getFrequencia() <= 0){
				this.mptProtocoloMedicamentos.setFrequencia(null);
			}
			
			procedimentoTerapeuticoFacade.validarCamposObrigatoriosMedicamento(
					medicamentosVO, mptProtocoloMedicamentos, mptProtocoloItemMedicamentos,
					unidadeSelecionada, listaParam,	viaSelecionada,	aprazamentoSelecionado, 
					unidHorasCorrer, unidadeInfusaoSelecionada);
			
			if (unidadeInfusaoSelecionada != null) {
				mptProtocoloMedicamentos.setTvaSeq(unidadeInfusaoSelecionada.getSeq());
			}
						
			if(validaCampoObservacao()){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_TAMANHO_CAMPO_OBSERVACAO_MEDICAMENTO");
			}else{
				obterDados();
							
				if(!this.isDiaModificado){
					this.procedimentoTerapeuticoFacade.inserirMedicamento(this.mptProtocoloMedicamentos , this.mptProtocoloItemMedicamentos, this.vpsSeq, this.parametroSelecionado, listaParam, listaParamExcluidos);
				}
				if(this.parametroSelecionado != null){
						if(!atualizarDiasModificadosMedicamentos()){
							this.paramDoseVO = new ParametroDoseUnidadeVO();
							this.apresentarMsgNegocio(Severity.INFO, "MSG_MEDICAMENTO_ATUALIZADO");
							}
					}else{
						this.apresentarMsgNegocio(Severity.INFO, "MSG_MEDICAMENTO_SALVO");
						limpar();
					}
			}	
				
		}catch(BaseListException e ) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				mensagensMedicamentoDescUniTempo(aghuNegocioException);
				mensagensVelInfusaoUniInfusaoDoseUni(aghuNegocioException);
				mensagensViaAprazComplementoLista(aghuNegocioException);
			}
		}
		catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void obterDados() {
		AfaMedicamento afaMedicamento = null;
		AfaFormaDosagem afaFormaDosagem = null;	
			if(this.medicamentosVO != null){
				afaMedicamento = this.procedimentoTerapeuticoFacade.obterAfaMedicamentoPorChavePrimaria(this.medicamentosVO.getMedMatCodigo());
				this.mptProtocoloItemMedicamentos.setMedMatCodigo(this.medicamentosVO.getMedMatCodigo());
			}
			if(this.unidadeSelecionada != null){
				 afaFormaDosagem = this.procedimentoTerapeuticoFacade.obterAfaFormaDosagemPorChavePrimaria(this.unidadeSelecionada.getFdsSeq());
				 this.mptProtocoloItemMedicamentos.setAfaFormaDosagem(afaFormaDosagem);
			}
			if(unidadeInfusaoSelecionada != null){
				this.mptProtocoloMedicamentos.setTipoVelocAdministracoes(unidadeInfusaoSelecionada);
			}
			this.mptProtocoloItemMedicamentos.setAfaMedicamento(afaMedicamento);
			if(this.diluenteSelecionado != null){
				this.mptProtocoloMedicamentos.setMedMatCodigoDiluente(this.diluenteSelecionado.getMatCodigo());
			}else{
				this.mptProtocoloMedicamentos.setMedMatCodigoDiluente(null);
			}
			
			if(this.parametroSelecionado == null){
				List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO = this.cadastrarProtocoloController.pesquisarListaTratamento();
				if(!listaProtocoloMedicamentosVO.isEmpty()){
					Integer count = listaProtocoloMedicamentosVO.size() + 1;
					ordem = count.shortValue();
					this.mptProtocoloMedicamentos.setOrdem(ordem);
				}else{
					ordem = 1;
					this.mptProtocoloMedicamentos.setOrdem(ordem);
				}
			}
			this.mptProtocoloMedicamentos.setVadSigla(this.viaSelecionada);
			this.mptProtocoloMedicamentos.setTfqSeq(this.aprazamentoSelecionado);
	}

	private void mensagensViaAprazComplementoLista(
			BaseException aghuNegocioException) {
		if(aghuNegocioException.getMessage().equals("CAMPO_VIA_NULO")){
			apresentarMsgNegocio("suggestion_via", Severity.ERROR, "CAMPO_VIA_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_APRAZAMENTO_NULO")){
			apresentarMsgNegocio("suggestion_aprazamento", Severity.ERROR, "CAMPO_APRAZAMENTO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_COMPLEMENTO_NULO")){
			apresentarMsgNegocio("complemento", Severity.ERROR, "CAMPO_COMPLEMENTO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("LISTA_PARAMETRO_DOSE_MEDICAMENTO_VAZIA")){
			apresentarMsgNegocio(Severity.ERROR, "LISTA_PARAMETRO_DOSE_MEDICAMENTO_VAZIA");
		}
		if(aghuNegocioException.getMessage().equals("MSG_CAMPO_FREQUENCIA_MEDICAMENTO") && this.isValida){
			this.apresentarMsgNegocio("frequencia", Severity.ERROR, "MSG_CAMPO_FREQUENCIA_MEDICAMENTO");
		}
	
	}

	private void mensagensVelInfusaoUniInfusaoDoseUni(
			BaseException aghuNegocioException) {
		if(aghuNegocioException.getMessage().equals("CAMPO_VELOCIDADE_DE_INFUSAO_NULO")){
			apresentarMsgNegocio("velocidadeInfusao", Severity.ERROR, "CAMPO_VELOCIDADE_DE_INFUSAO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_UNIDADE_DE_INFUSAO_NULO")){
			apresentarMsgNegocio("unidadeInfusao", Severity.ERROR, "CAMPO_UNIDADE_DE_INFUSAO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_DOSE_NULO")){
			apresentarMsgNegocio("dose", Severity.ERROR, "CAMPO_DOSE_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_UNIDADE_NULO")){
			apresentarMsgNegocio("unidade", Severity.ERROR, "CAMPO_UNIDADE_NULO");
		}
	}

	private void mensagensMedicamentoDescUniTempo(
			BaseException aghuNegocioException) {
		if(aghuNegocioException.getMessage().equals("CAMPO_MEDICAMENTO_NULO")){
			apresentarMsgNegocio("suggestion_medicamento", Severity.ERROR, "CAMPO_MEDICAMENTO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_DESCRICAO_NULO")){
			apresentarMsgNegocio("medDescricao", Severity.ERROR, "CAMPO_DESCRICAO_NULO");
		}
		if(aghuNegocioException.getMessage().equals("CAMPO_UNIDADE_DE_TEMPO_NULO")){
			apresentarMsgNegocio("unidadeTempo", Severity.ERROR, "CAMPO_UNIDADE_DE_TEMPO_NULO");
		}
	}
	
	
	public Boolean verificarProtocoloMdtoDiaModificado(){
		Boolean retorno;
		if(this.parametroSelecionado != null){
			this.listaMptProtocoloMedicamentosDia = this.procedimentoTerapeuticoFacade.obterProtocoloMdtoDiaModificado(parametroSelecionado.getPtmSeq());
			if(!this.listaMptProtocoloMedicamentosDia.isEmpty()){
				retorno = true;
			}else{
				retorno = false;
			}
			if(readOnly){
				retorno = true;
			}
		}else{
			retorno = false;
		}
		return retorno;
	}
	
	public Boolean atualizarDiasModificadosMedicamentos(){
		Boolean retorno = false;
		if(this.isDiaModificado){
		  MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia = this.procedimentoTerapeuticoFacade.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(parametroSelecionado.getPtmSeq(), this.dia);		  
		  this.procedimentoTerapeuticoFacade.atualizarDiaModificadoMedicamentos(this.mptProtocoloMedicamentos, mptProtocoloMedicamentosDia);
		}else{
			if(!this.listaMptProtocoloMedicamentosDia.isEmpty()){
				for(MptProtocoloMedicamentosDia item : this.listaMptProtocoloMedicamentosDia){
					if(item.getModificado()){
						retorno = true;
						RequestContext.getCurrentInstance().execute("PF('modal_dias_modificados').show()");
						break;
					}
				}
			}
		}
		return retorno;
	}
	
	public void alterarTodosDiasModificados(){		
		this.procedimentoTerapeuticoFacade.atualizarTodosDiasModificadosMedicamentos(this.mptProtocoloMedicamentos, this.listaMptProtocoloMedicamentosDia);
		this.apresentarMsgNegocio(Severity.INFO, "MSG_MEDICAMENTO_ATUALIZADO");
		
	}
	
	public void alterarDiasModificados(){		
		this.procedimentoTerapeuticoFacade.atualizarDiasModificadosMedicamentos(this.mptProtocoloMedicamentos, this.listaMptProtocoloMedicamentosDia);		
		this.apresentarMsgNegocio(Severity.INFO, "MSG_MEDICAMENTO_ATUALIZADO");
		
	}
	
	public List<AfaMedicamento> pesquisarDiluente(){
		if(this.medicamentosVO != null && this.medicamentosVO.getMedMatCodigo() != null){
				return this.procedimentoTerapeuticoFacade.pesquisaDiluente(this.medicamentosVO.getMedMatCodigo());
			}else{
				return null;
			}
	}

	public void validaFrequencia(){
		if(this.aprazamentoSelecionado != null){
			if(!this.aprazamentoSelecionado.getIndDigitaFrequencia()){
				this.mptProtocoloMedicamentos.setFrequencia(null);
				this.setIsFrequencia(true);
			}else{
				if(this.mptProtocoloMedicamentos.getFrequencia() != null && this.mptProtocoloMedicamentos.getFrequencia() <= 0){
					this.mptProtocoloMedicamentos.setFrequencia(null);
				}
			}
		}else{
			if(this.mptProtocoloMedicamentos.getFrequencia() != null && this.mptProtocoloMedicamentos.getFrequencia() <= 0){
				this.mptProtocoloMedicamentos.setFrequencia(null);
			}
			this.setIsFrequencia(false);
		}
		
		validaCampoFrequencia();
	}

	
	public Boolean validaCampoObservacao(){
		if(this.mptProtocoloItemMedicamentos != null && this.mptProtocoloItemMedicamentos.getObservacao() != null && this.mptProtocoloItemMedicamentos.getObservacao().length() > 120){
			return true;
		}else{
			return false;
		}
	}
	
	public void renderizarUnidadeBase(){
		if(paramDoseVO != null && paramDoseVO.getTipoCalculo() != null){
			if(MOSTELLER.equals(paramDoseVO.getTipoCalculo()) || 
					DUBOIS.equals(paramDoseVO.getTipoCalculo()) ||
					HAYCOCK.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = BARRA_M2;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
			if(PESO.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = BARRA_KG;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
			if(CALVERT.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = TRES_ESPACOS;
				desabilitarCampoUnidade = Boolean.TRUE;
				paramDoseVO.setAfaFormaDosagemVO(null);
				modificarLabelDoseUnitaria();
			}
			if(FIXO.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = TRES_ESPACOS;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
		} else {
			unidadeBase = TRES_ESPACOS;
			desabilitarCampoUnidade = Boolean.FALSE;
			modificarLabelDoseUnitaria();
		}
	}
	
	public void adicionarMedicamentoCalculo(){
		
		if(paramDoseVO.getSeq() == null){
			paramDoseVO.setSeq(seqNegativa--);
		}
		preencherUnidadeBaseCalculo();
		validarCamposObrigatorios();
		
	}

	private void validarCamposObrigatorios() {
		try {
			procedimentoTerapeuticoFacade.validarCamposObrigatoriosParametroDose(paramDoseVO, desabilitarCampoUnidade);
			habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
			procedimentoTerapeuticoFacade.validarPesoOuIdadeParamDose(listaParam, paramDoseVO);
			procedimentoTerapeuticoFacade.validarPesoOuIdadeMinimoMaiorQueMaximo(paramDoseVO);
			procedimentoTerapeuticoFacade.validarSobreposicaoDeFaixaParamDose(listaParam, paramDoseVO);
			if(isEdicaoCalculoDose) {
				isEdicaoCalculoDose = Boolean.FALSE;
				paramDoseVO.setIsEdicao(Boolean.FALSE);
				for (ParametroDoseUnidadeVO vo : listaParam) {
					if(vo.getSeq().equals(paramDoseVO.getSeq())){
						vo.setAfaFormaDosagem(paramDoseVO.getAfaFormaDosagem());
						vo.setAfaFormaDosagemVO(paramDoseVO.getAfaFormaDosagemVO());
						vo.setAlertaCalculoDose(paramDoseVO.getAlertaCalculoDose());
						vo.setComboUnidade(paramDoseVO.getComboUnidade());
						vo.setDescricao(paramDoseVO.getDescricao());
						vo.setDose(paramDoseVO.getDose());
						vo.setDoseMaximaUnitaria(paramDoseVO.getDoseMaximaUnitaria());
						vo.setFdsSeq(paramDoseVO.getFdsSeq());
						vo.setIdadeMaxima(paramDoseVO.getIdadeMaxima());
						vo.setIdadeMinima(paramDoseVO.getIdadeMinima());
						vo.setIsEdicao(Boolean.FALSE);
						vo.setMedMatCodigo(paramDoseVO.getMedMatCodigo());
						vo.setMptProtocoloItemMedicamentos(paramDoseVO.getMptProtocoloItemMedicamentos());
						vo.setPesoMaximo(paramDoseVO.getPesoMaximo());
						vo.setPesoMinimo(paramDoseVO.getPesoMinimo());
						vo.setPmiSeq(paramDoseVO.getPmiSeq());
//						vo.setSeq(paramDoseVO.getSeq());
						vo.setTipoCalculo(paramDoseVO.getTipoCalculo());
						vo.setUnidBaseCalculo(paramDoseVO.getUnidBaseCalculo());
						vo.setUnidIdade(paramDoseVO.getUnidIdade());
					}
				}
			} else {
				listaParam.add(paramDoseVO);
			}
			limparCamposParametro();
		} catch (BaseListException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private void preencherUnidadeBaseCalculo() {
		if(paramDoseVO.getTipoCalculo() != null){
			if(MOSTELLER.equals(paramDoseVO.getTipoCalculo()) || 
					DUBOIS.equals(paramDoseVO.getTipoCalculo()) ||
					HAYCOCK.equals(paramDoseVO.getTipoCalculo())){
				paramDoseVO.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.M);
			}
			if(PESO.equals(paramDoseVO.getTipoCalculo())){
				paramDoseVO.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.K);
			}
			if(CALVERT.equals(paramDoseVO.getTipoCalculo())){
				paramDoseVO.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.A);
			}
			if(FIXO.equals(paramDoseVO.getTipoCalculo())){
				paramDoseVO.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.D);
			}
		}
	}
	public void modificarLabelDoseUnitaria(){
		if(this.paramDoseVO != null){
			if(this.paramDoseVO.getTipoCalculo() != null){
				if(DominioCalculoDose.C.equals(this.paramDoseVO.getTipoCalculo())){
					labelDoseUnitaria = LABEL_DOSE_AUC;
				} else {
					labelDoseUnitaria = LABEL_DOSE_UNITARIA;
				}
			} else {
				labelDoseUnitaria = LABEL_DOSE_UNITARIA;
			}
		} else {
			labelDoseUnitaria = LABEL_DOSE_UNITARIA;
		}
	}
	public void limparCamposParametro() {
		this.paramDoseVO = new ParametroDoseUnidadeVO();
		this.isEdicaoCalculoDose = Boolean.FALSE;
		this.unidadeBase = TRES_ESPACOS;
		this.desabilitarCampoUnidade = Boolean.FALSE;
		this.labelDoseUnitaria = LABEL_DOSE_UNITARIA;
		this.parametroDoseSelecionado = new ParametroDoseUnidadeVO();
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
	}
	public void editarCalculoDose(ParametroDoseUnidadeVO item) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		PropertyUtils.copyProperties(paramDoseVO, item);
		renderizarUnidadeBase();
		isEdicaoCalculoDose = Boolean.TRUE;
		if(listaParam.size() == 1){
			habilitarAdicionarParamDose = Boolean.FALSE;
		}
	}
	public void excluirCalculoDose(ParametroDoseUnidadeVO item) {
		listaParam.remove(item);
		
		if(parametroSelecionado != null && item.getSeq() > 0){
//			procedimentoTerapeuticoFacade.removerParametroDose(item);
			listaParamExcluidos.add(item);
		}
		
		if(listaParam.isEmpty()){
			habilitarAdicionarParamDose = Boolean.FALSE;
		} else {
			habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(listaParam, paramDoseVO);
		}
	}
	
	public void cancelarEdicaoParametro(){
		limparCamposParametro();
	}
	private Boolean validaCampoFrequencia(){
		this.isValida = false;
		if(this.aprazamentoSelecionado != null && this.aprazamentoSelecionado.getIndDigitaFrequencia()){
			this.isValida = true;
		}else if(this.mptProtocoloMedicamentos.getFrequencia() != null && this.mptProtocoloMedicamentos.getFrequencia() <= 0){
			this.mptProtocoloMedicamentos.setFrequencia(null);
			this.isValida = true;
		}else if(this.mptProtocoloMedicamentos.getFrequencia() == null && !this.isFrequencia && this.aprazamentoSelecionado != null){
			this.isValida = true;
		}
				
		return this.isValida;
	}
	public DominioPadronizado getPadronizacao() {
		return padronizacao;
	}
	public void setPadronizacao(DominioPadronizado padronizacao) {
		this.padronizacao = padronizacao;
	}
	public MptProtocoloMedicamentos getMptProtocoloMedicamentos() {
		return mptProtocoloMedicamentos;
	}
	public void setMptProtocoloMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos) {
		this.mptProtocoloMedicamentos = mptProtocoloMedicamentos;
	}
	public MptProtocoloItemMedicamentos getMptProtocoloItemMedicamentos() {
		return mptProtocoloItemMedicamentos;
	}
	public void setMptProtocoloItemMedicamentos(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos) {
		this.mptProtocoloItemMedicamentos = mptProtocoloItemMedicamentos;
	}
	public MedicamentosVO getMedicamentosVO() {
		return medicamentosVO;
	}
	public void setMedicamentosVO(MedicamentosVO medicamentosVO) {
		this.medicamentosVO = medicamentosVO;
		if(medicamentosVO != null){
			setMedicamentosVOAuxiliar(medicamentosVO);
		}
	}
	public MpmTipoFrequenciaAprazamento getAprazamentoSelecionado() {
		return aprazamentoSelecionado;
	}
	public void setAprazamentoSelecionado(MpmTipoFrequenciaAprazamento aprazamentoSelecionado) {
		this.aprazamentoSelecionado = aprazamentoSelecionado;
	}
	public AfaFormaDosagemVO getUnidadeSelecionada() {
		return unidadeSelecionada;
	}
	public void setUnidadeSelecionada(AfaFormaDosagemVO unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}
	public AfaViaAdministracao getViaSelecionada() {
		return viaSelecionada;
	}
	public void setViaSelecionada(AfaViaAdministracao viaSelecionada) {
		this.viaSelecionada = viaSelecionada;
	}
	public Boolean getTodasVias() {
		return todasVias;
	}
	public void setTodasVias(Boolean todasVias) {
		this.todasVias = todasVias;
	}
	public Short getCorrerEm() {
		return correrEm;
	}
	public void setCorrerEm(Short correrEm) {
		this.correrEm = correrEm;
	}
	public AfaTipoVelocAdministracoes getUnidadeInfusaoSelecionada() {
		return unidadeInfusaoSelecionada;
	}
	public void setUnidadeInfusaoSelecionada(AfaTipoVelocAdministracoes unidadeInfusaoSelecionada) {
		this.unidadeInfusaoSelecionada = unidadeInfusaoSelecionada;
	}
	public List<AfaMensagemMedicamento> getListaMensagemMedicamento() {
		return listaMensagemMedicamento;
	}
	public void setListaMensagemMedicamento(List<AfaMensagemMedicamento> listaMensagemMedicamento) {
		this.listaMensagemMedicamento = listaMensagemMedicamento;
	}
	public Integer getVpsSeq() {
		return vpsSeq;
	}
	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}
	public List<MedicamentosVO> getListaMedicamentosVO() {
		return listaMedicamentosVO;
	}
	public void setListaMedicamentosVO(List<MedicamentosVO> listaMedicamentosVO) {
		this.listaMedicamentosVO = listaMedicamentosVO;
	}
	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}
	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}
	public List<AfaFormaDosagemVO> getListaFormaDosagemVO() {
		return listaFormaDosagemVO;
	}
	public void setListaFormaDosagemVO(List<AfaFormaDosagemVO> listaFormaDosagemVO) {
		this.listaFormaDosagemVO = listaFormaDosagemVO;
	}
	public ProtocoloMedicamentoSolucaoCuidadoVO getParametroSelecionado() {
		return parametroSelecionado;
	}
	public void setParametroSelecionado(ProtocoloMedicamentoSolucaoCuidadoVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	public List<MptProtocoloMedicamentosDia> getListaMptProtocoloMedicamentosDia() {
		return listaMptProtocoloMedicamentosDia;
	}
	public void setListaMptProtocoloMedicamentosDia(List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia) {
		this.listaMptProtocoloMedicamentosDia = listaMptProtocoloMedicamentosDia;
	}
	public Boolean getIsDiaModificado() {
		return isDiaModificado;
	}
	public void setIsDiaModificado(Boolean isDiaModificado) {
		this.isDiaModificado = isDiaModificado;
	}
	public Short getDia() {
		return dia;
	}
	public void setDia(Short dia) {
		this.dia = dia;
	}
	public AfaMedicamento getDiluenteSelecionado() {
		return diluenteSelecionado;
	}
	public void setDiluenteSelecionado(AfaMedicamento diluenteSelecionado) {
		this.diluenteSelecionado = diluenteSelecionado;
	}
	public Boolean getMedIndExigeObservacao() {
		return medIndExigeObservacao;
	}
	public void setMedIndExigeObservacao(Boolean medIndExigeObservacao) {
		this.medIndExigeObservacao = medIndExigeObservacao;
	}
	public Boolean getMensagem() {
		return mensagem;
	}
	public void setMensagem(Boolean mensagem) {
		this.mensagem = mensagem;
	}
	public Boolean getIsFrequencia() {
		return isFrequencia;
	}
	public void setIsFrequencia(Boolean isFrequencia) {
		this.isFrequencia = isFrequencia;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public Boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	public ParametroDoseUnidadeVO getParamDoseVO() {
		return paramDoseVO;
	}
	public void setParamDoseVO(ParametroDoseUnidadeVO paramDoseVO) {
		this.paramDoseVO = paramDoseVO;
	}
	public String getUnidadeBase() {
		return unidadeBase;
	}
	public void setUnidadeBase(String unidadeBase) {
		this.unidadeBase = unidadeBase;
	}
	public Boolean getHabilitarAdicionarParamDose() {
		return habilitarAdicionarParamDose;
	}
	public void setHabilitarAdicionarParamDose(Boolean habilitarAdicionarParamDose) {
		this.habilitarAdicionarParamDose = habilitarAdicionarParamDose;
	}
	public List<ParametroDoseUnidadeVO> getListaParam() {
		return listaParam;
	}
	public void setListaParam(List<ParametroDoseUnidadeVO> listaParam) {
		this.listaParam = listaParam;
	}
	public Boolean getIsEdicaoCalculoDose() {
		return isEdicaoCalculoDose;
	}
	public void setIsEdicaoCalculoDose(Boolean isEdicaoCalculoDose) {
		this.isEdicaoCalculoDose = isEdicaoCalculoDose;
	}
	public Integer getSeqNegativa() {
		return seqNegativa;
	}
	public void setSeqNegativa(Integer seqNegativa) {
		this.seqNegativa = seqNegativa;
	}
	public ParametroDoseUnidadeVO getParamDoseEditado() {
		return paramDoseEditado;
	}
	public void setParamDoseEditado(ParametroDoseUnidadeVO paramDoseEditado) {
		this.paramDoseEditado = paramDoseEditado;
	}
	public List<AfaFormaDosagemVO> getListaFormaDosagemVOParamDose() {
		return listaFormaDosagemVOParamDose;
	}
	public void setListaFormaDosagemVOParamDose(List<AfaFormaDosagemVO> listaFormaDosagemVOParamDose) {
		this.listaFormaDosagemVOParamDose = listaFormaDosagemVOParamDose;
	}
	public String getLabelDoseUnitaria() {
		return labelDoseUnitaria;
	}
	public void setLabelDoseUnitaria(String labelDoseUnitaria) {
		this.labelDoseUnitaria = labelDoseUnitaria;
	}
	public ParametroDoseUnidadeVO getParametroDoseSelecionado() {
		return parametroDoseSelecionado;
	}
	public void setParametroDoseSelecionado(ParametroDoseUnidadeVO parametroDoseSelecionado) {
		this.parametroDoseSelecionado = parametroDoseSelecionado;
	}
	public Boolean getDesabilitarCampoUnidade() {
		return desabilitarCampoUnidade;
	}
	public void setDesabilitarCampoUnidade(Boolean desabilitarCampoUnidade) {
		this.desabilitarCampoUnidade = desabilitarCampoUnidade;
	}
	public String getBarra() {
		return barra;
	}
	public void setBarra(String barra) {
		this.barra = barra;
	}
	public Boolean getIsValida() {
		return isValida;
	}
	public void setIsValida(Boolean isValida) {
		this.isValida = isValida;
	}
	public MedicamentosVO getMedicamentosVOAuxiliar() {
		return medicamentosVOAuxiliar;
	}
	public void setMedicamentosVOAuxiliar(MedicamentosVO medicamentosVOAuxiliar) {
		this.medicamentosVOAuxiliar = medicamentosVOAuxiliar;
	}
	public MptProtocoloMedicamentosDia getMptProtocoloMedicamentosDia() {
		return mptProtocoloMedicamentosDia;
	}
	public void setMptProtocoloMedicamentosDia(
			MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia) {
		this.mptProtocoloMedicamentosDia = mptProtocoloMedicamentosDia;
	}

	public List<ParametroDoseUnidadeVO> getListaParamExcluidos() {
		return listaParamExcluidos;
	}

	public void setListaParamExcluidos(
			List<ParametroDoseUnidadeVO> listaParamExcluidos) {
		this.listaParamExcluidos = listaParamExcluidos;
	}
}
