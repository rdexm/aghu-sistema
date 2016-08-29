package br.gov.mec.aghu.prescricaomedica.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

public class CadastroComparecimentoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2948215317128536684L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private CadastroOutrosAtestadosController cadastroOutrosAtestadosController;

	@Inject
	private CadastroAtestadoAcompanhamentoController cadastroAtestadoAcompanhamentoController;
	
	@Inject
	private CadastroAtestadoFgtsPisPasepController cadastroAtestadoFgtsPisPasepController;	
	
//	@Inject
//	private RelatorioAtestadosController relatorioAtestadosController;
	
	// Recebido por parâmetro
	private MpmAltaSumario mpmAltaSumario;

	private MamAtestados mamAtestado;
	private MamAtestados itemSelecionado;
	// Workaround para funcionar o atributo selection da tabela.
	private MamAtestados itemAux;
	private List<MamAtestados> mamAtestados;
	private boolean modoEdicao;
	private String declaracaoParte1;
	private AghParametros pTipoAtestado;
	private Integer atdSeq;
	private Integer apaSeq;
	private Integer indexSelecionado;
	private Integer indexAnterior;
	private Integer pacCodigo;
	
	private boolean buscaArvoreCid = false;
	
	/** #46218 */
	private final static String ATESTADO = "ATESTADO";
	private String listaOrigem;
	@EJB
	private IPacienteFacade pacienteFacade; 
	
	private MamAtestados mamAtestadoParametro = new MamAtestados();
	

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void inicio() {
		validaParametrosRequest();
		
		if (!buscaArvoreCid) {
			if(this.listaOrigem != null && ATESTADO.trim().equals(this.listaOrigem.trim()) && this.atdSeq != null){
				this.recuperaParametroAtestadoComparecimento();
				pesquisarPorAtdSeq(this.atdSeq);
				//Alta sumario somente com pacientes para atender as mudanças do #46218
				this.mpmAltaSumario = new MpmAltaSumario();
				this.mpmAltaSumario.setPaciente(this.pacienteFacade.obterPaciente(Integer.valueOf(this.pacCodigo)));
			}else{
				if(this.apaSeq == null && this.atdSeq != null){
						try {
							this.apaSeq = this.prescricaoMedicaFacade.recuperarAtendimentoPaciente(this.atdSeq);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
				}
				if (this.atdSeq != null && this.listaOrigem != null && !this.listaOrigem.equals("") ) {
					try {
						this.mpmAltaSumario = this.prescricaoMedicaFacade
								.recuperarSumarioAlta(this.atdSeq, this.apaSeq);
	
						if ((this.mpmAltaSumario == null)
								|| ((this.mpmAltaSumario != null) && this
										.possuiEmergencia(this.atdSeq, this.apaSeq,
												this.mpmAltaSumario.getId().getSeqp()))) {
	
							this.mpmAltaSumario = this.prescricaoMedicaFacade
									.gerarAltaSumario(this.atdSeq, this.apaSeq,
											listaOrigem);
	
						}
						
						if (this.pacCodigo != null){
							this.mpmAltaSumario.setPaciente(this.pacienteFacade.obterPaciente(Integer.valueOf(pacCodigo)));
						}
					} catch (BaseException e) {
						this.apresentarExcecaoNegocio(e);
					}
					
					
				}
				this.recuperaParametroAtestadoComparecimento();
				pesquisarPorAlta(this.mpmAltaSumario.getId().getApaAtdSeq(), this.mpmAltaSumario.getId().getApaSeq(), this.mpmAltaSumario.getId().getSeqp());
			}
			
			cadastroOutrosAtestadosController.setAltaSumario(this.mpmAltaSumario);
			cadastroOutrosAtestadosController.setpUso("S");
			cadastroOutrosAtestadosController.setListaOrigem(listaOrigem);
			cadastroOutrosAtestadosController.setAtsSeq(this.atdSeq.longValue());
			cadastroOutrosAtestadosController.setAtdSeq(this.atdSeq);
			cadastroOutrosAtestadosController.inicio();
			
			cadastroAtestadoAcompanhamentoController.setMpmAltaSumario(this.mpmAltaSumario);
			cadastroAtestadoAcompanhamentoController.setListaOrigem(listaOrigem);
			cadastroAtestadoAcompanhamentoController.setAtdSeq(this.atdSeq);
			cadastroAtestadoAcompanhamentoController.inicio();
			
			cadastroAtestadoFgtsPisPasepController.setMpmAltaSumario(this.mpmAltaSumario);
			cadastroAtestadoFgtsPisPasepController.setListaOrigem(listaOrigem);
			cadastroAtestadoFgtsPisPasepController.setAtdSeq(this.atdSeq);
			cadastroAtestadoFgtsPisPasepController.inicio();
			
			/** #46218 */
			
			this.indexSelecionado = 0;
			this.indexAnterior = 0;
			
			this.declaracaoParte1 = montarDeclaracaoParte1(this.mpmAltaSumario.getPaciente().getNome(), this.mpmAltaSumario.getPaciente().getProntuario());
			this.limpar();
		} else {
			this.indexSelecionado = 2;
			buscaArvoreCid = false;
		}
	}

	private void validaParametrosRequest() {
		if(getRequestParameter("altanAtdSeq") != null){
			this.atdSeq = Integer.valueOf(getRequestParameter("altanAtdSeq").trim());
		}
		if(getRequestParameter("altanListaOrigem") != null){
			this.listaOrigem = getRequestParameter("altanListaOrigem").trim();
		}
		if( getRequestParameter("altanApaSeq") != null){
			this.apaSeq = Integer.valueOf(getRequestParameter("altanApaSeq").trim());
		}
		if(getRequestParameter("pacCodigo") != null){
			this.pacCodigo = Integer.valueOf(getRequestParameter("pacCodigo").trim());
		}
	}
		
		/**
		 * VERIFICA SE O SUMÁRIO É DA EMERGENCIA
		 * @param altanAtdSeq
		 * @param altanApaSeq
		 * @param seqp
		 * @return
		 */
		private boolean possuiEmergencia(Integer altanAtdSeq, Integer altanApaSeq, Short seqp) {
			if (seqp != null) {
				try {
					return this.prescricaoMedicaFacade.verificarEmergencia(altanAtdSeq, altanApaSeq, seqp);
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
				}
			}
			return false;
		}	

	public String montarDeclaracaoParte1(String nomePaciente, Integer prontuario) {
		return getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P1").concat(nomePaciente)
				.concat(getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P2")).concat(" ").concat(prontuario.toString()).concat(",")
				.concat(" ");
	}

	public String montarDeclaracaoParte2(java.sql.Date dataCons) {
		String dataFormatada = null;

		if (dataCons != null) {
			SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			dataFormatada = sf.format(dataCons);
		}

		return getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P3").concat(" ").concat(dataFormatada);
	}

	public String montarDeclaracaoCompleta(String nomePaciente, Integer prontuario, java.sql.Date dataCons) {
		return this.montarDeclaracaoParte1(nomePaciente, prontuario).concat(this.montarDeclaracaoParte2(dataCons).concat("."));
	}

	public String montarDeclaracaoTruncada(String nomePaciente, Integer prontuario, java.sql.Date dataCons) {
		String texto = this.montarDeclaracaoCompleta(nomePaciente, prontuario, dataCons);
		if (texto.length() >= 30) {
			return texto.substring(0, 30);
		}
		return texto;
	}

	/**
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void renderAtestado(MpmAltaSumario altaSumarioAtual) throws BaseException {
		if (this.getMpmAltaSumario() == null || (altaSumarioAtual != null && !this.getMpmAltaSumario().getId().equals(altaSumarioAtual.getId()))) {
			this.setMpmAltaSumario(altaSumarioAtual);
			this.inicio();

			//CORREÇAO
			
			cadastroOutrosAtestadosController.setAltaSumario(altaSumarioAtual);
			cadastroOutrosAtestadosController.setpUso("S");
			cadastroOutrosAtestadosController.inicio();

			cadastroAtestadoAcompanhamentoController.setMpmAltaSumario(altaSumarioAtual);
			cadastroAtestadoAcompanhamentoController.inicio();
			
			cadastroAtestadoFgtsPisPasepController.setMpmAltaSumario(altaSumarioAtual);
			cadastroAtestadoFgtsPisPasepController.inicio();
		}
	}

	private void recuperaParametroAtestadoComparecimento() {
		try {
			pTipoAtestado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ATESTADO_COMPARECIMENTO_INTERNACAO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	//Retirou pela C2 e C3
	/*public void pesquisar() {
		this.mamAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipo(this.mamAtestado.getAipPacientes().getCodigo(), pTipoAtestado
				.getVlrNumerico().shortValue());
	}*/
	public void pesquisarPorAtdSeq(Integer atdSeq){
		this.mamAtestados =	this.ambulatorioFacade.obterAtestadosPorAtdSeqTipo(atdSeq, pTipoAtestado
				.getVlrNumerico().shortValue());
	}

	public void pesquisarPorAlta(Integer apaAtdSeq, Integer apaSeq, Short seqP){
		this.mamAtestados = this.ambulatorioFacade.obterAtestadosPorSumarioAltaTipo(apaAtdSeq, apaSeq, seqP, pTipoAtestado
				.getVlrNumerico().shortValue());
	}
	public void gravar() {
		try {
			
			if(this.mamAtestado.getDthrCons() == null){
				throw new ApplicationBusinessException("Um valor é obrigatório para data", Severity.ERROR);
			}
				
			this.mamAtestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
			this.mamAtestado.setIndImpresso(Boolean.FALSE);
			/*this.mamAtestado.setDataInicial(this.mamAtestado.getDthrCons());
			this.mamAtestado.setDataFinal(this.mamAtestado.getDthrCons());*/

			/** #46218 */
			if(this.listaOrigem.trim().equals(ATESTADO)){
				this.mamAtestado.setAltaSumario(null);
				this.mamAtestado.setAtendimento(new AghAtendimentos());
				this.mamAtestado.getAtendimento().setSeq(this.atdSeq);
			}

			this.ambulatorioFacade.persistirMamAtestado(this.mamAtestado, false);

			if (this.modoEdicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_ALTERACAO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_INCLUSAO_SUCESSO");
			}
			this.modoEdicao = Boolean.FALSE;
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editar() {
		this.mamAtestado = this.itemSelecionado;
		this.modoEdicao = Boolean.TRUE;
	}

	public void excluir() {
		try {
			this.ambulatorioFacade.excluirAtestadoAcompanhamento(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_EXCLUSAO_SUCESSO");
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao() {
		this.modoEdicao = Boolean.FALSE;
		this.limpar();
	}
	
	public void limparAtestados() {
		this.cancelarEdicao();
		this.cadastroAtestadoAcompanhamentoController.cancelarEdicao();
		this.cadastroOutrosAtestadosController.cancelar();		
		this.cadastroAtestadoFgtsPisPasepController.cancelarEdicao();
	}

	public void limpar() {
		this.mamAtestado = new MamAtestados();
		this.mamAtestado.setDthrCriacao(new Date());
		this.mamAtestado.setAipPacientes(this.mpmAltaSumario.getPaciente());
		this.mamAtestado.setMamTipoAtestado(this.ambulatorioFacade.obterTipoAtestadoPorSeq(pTipoAtestado.getVlrNumerico().shortValue()));
		this.mamAtestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
		this.mamAtestado.setIndImpresso(Boolean.TRUE);
		this.mamAtestado.setDthrCons(null);
		this.mamAtestado.setAltaSumario(this.mpmAltaSumario);
		this.mamAtestado.setNroVias(Byte.valueOf("1"));
	}
	
	/**
	 * #46252 - Chama a impressão do relatório #46485
	 */
	public void imprimirAtestado() {
//		if (this.itemSelecionado != null) {
//			try {
//				AtestadoVO atestado = this.prescricaoMedicaFacade
//						.obterDocumentoPacienteAtestado(this.itemSelecionado.getSeq(), false);
//				
//				this.relatorioAtestadosController.setAtestado(atestado);
//				this.relatorioAtestadosController.setDescricaoDocumento("Acompanhamento");
//				this.relatorioAtestadosController.directPrint();
//			} catch (ApplicationBusinessException e) {
//				apresentarExcecaoNegocio(e);
//			}
//		} else {
//			apresentarMsgNegocio(Severity.INFO, "MAM_SELECIONE_ATESTADO_IMPRIMIR");
//		}
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public MpmAltaSumario getMpmAltaSumario() {
		return mpmAltaSumario;
	}

	public void setMpmAltaSumario(MpmAltaSumario mpmAltaSumario) {
		this.mpmAltaSumario = mpmAltaSumario;
	}

	public MamAtestados getMamAtestado() {
		return mamAtestado;
	}

	public void setMamAtestado(MamAtestados mamAtestado) {
		this.mamAtestado = mamAtestado;
	}

	public MamAtestados getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MamAtestados itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MamAtestados getItemAux() {
		return itemAux;
	}

	public void setItemAux(MamAtestados itemAux) {
		this.itemAux = itemAux;
	}

	public List<MamAtestados> getMamAtestados() {
		return mamAtestados;
	}

	public void setMamAtestados(List<MamAtestados> mamAtestados) {
		this.mamAtestados = mamAtestados;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getDeclaracaoParte1() {
		return declaracaoParte1;
	}

	public void setDeclaracaoParte1(String declaracaoParte1) {
		this.declaracaoParte1 = declaracaoParte1;
	}

	public AghParametros getpTipoAtestado() {
		return pTipoAtestado;
	}

	public void setpTipoAtestado(AghParametros pTipoAtestado) {
		this.pTipoAtestado = pTipoAtestado;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getIndexSelecionado() {
		return indexSelecionado;
	}

	public void setIndexSelecionado(Integer indexSelecionado) {
		this.indexSelecionado = indexSelecionado;
	}

	public Integer getIndexAnterior() {
		return indexAnterior;
	}

	public void setIndexAnterior(Integer indexAnterior) {
		this.indexAnterior = indexAnterior;
	}

	public String getListaOrigem() {
		return listaOrigem;
	}
	
	public MamAtestados getMamAtestadoParametro() {
		return mamAtestadoParametro;
	}

	public void setMamAtestadoParametro(MamAtestados mamAtestadoParametro) {
		this.mamAtestadoParametro = mamAtestadoParametro;
	}

	public boolean isBuscaArvoreCid() {
		return buscaArvoreCid;
	}

	public void setBuscaArvoreCid(boolean buscaArvoreCid) {
		this.buscaArvoreCid = buscaArvoreCid;
	}
}