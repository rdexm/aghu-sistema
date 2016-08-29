package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAinServInterna;
import br.gov.mec.aghu.model.VAinServInternaId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class LaudoAIHController extends ActionController {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";


	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private enum CadastroInternacaoONExceptionCode implements
	BusinessExceptionCode {CID_PRINCIPAL_EXIGE_SECUNDARIO, AIN_00301}
	
	private static final Log LOG = LogFactory.getLog(LaudoAIHController.class);

	private static final long serialVersionUID = 4968542297445134863L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private RelatorioLaudoAIHController relatorioLaudoAIHController;

	/*
	 * PARÂMETROS DE CONVERSAÇÃO
	 */
	private Integer conNumero;
	private Integer pacCodigo;
	private Long seq;
	private Long trgSeq;
	private Long rgtSeq;
	private DominioSimNao assinarLaudo;
	private String voltarPara;

	/*
	 * CAMPOS DO LAUDO
	 */
	private MamLaudoAih mamLaudoAih;
	private DominioNaturezaFichaAnestesia prioridade;
	private DominioNaturezaFichaAnestesia[] itensPrioridade = new DominioNaturezaFichaAnestesia[] { DominioNaturezaFichaAnestesia.ELE, DominioNaturezaFichaAnestesia.URG }; // Eletiva
																																											// mantém
																																											// valor
																																											// 1,
																																											// Urgência
																																											// mantém
																																											// valor
																																											// 3

	private boolean permiteEdicao = true;
	private boolean habilitaCidSecundario = false;
	private String origemAmbulatorio = "ambulatorio"; // Se chamado a partir do Ambulatório, selecionar prioridade ELETIVA, senão, URGENTE.

	private VAinServInterna vAinServInterna; // Recebe a Equipe selecionada na SB

	private VFatSsmInternacaoVO vFatSsmInternacaoVO; // Recebe o Procedimento selecionado na SB
	
	
	public void inicio() {
	 
		this.habilitaCidSecundario = false;
		this.permiteEdicao = true;

		mamLaudoAih = new MamLaudoAih();

		if (seq == null) {

			if (conNumero != null) {
				mamLaudoAih.setConNumero(conNumero);
			}

			this.vAinServInterna = null;
			this.vFatSsmInternacaoVO = null;
					
			// Se paciente não vier por parâmetro deve ser obtido através da consulta.
			if (pacCodigo != null) {
				mamLaudoAih.setPaciente(this.pacienteFacade.obterPacientePorCodigo(this.pacCodigo));

			} else if (pacCodigo == null && conNumero != null) {
				mamLaudoAih.setPaciente(this.ambulatorioFacade.obterPacienteConsulta(this.ambulatorioFacade.obterConsulta(mamLaudoAih.getConNumero())));
			}

			if (this.voltarPara.equals(origemAmbulatorio)) {
				prioridade = DominioNaturezaFichaAnestesia.ELE;
			} else {
				prioridade = DominioNaturezaFichaAnestesia.URG;
			}

			if (conNumero != null) {
				// Seta a data provável de internação
				AacConsultas consulta = this.ambulatorioFacade.obterConsultaPorNumero(conNumero);
				if (consulta != null) {
					mamLaudoAih.setDtProvavelInternacao(consulta.getDtConsulta());
				}

				// Executa C7, se retornar resultado preenche a suggestion com a especialidade retornada.
				List<AghEspecialidades> especialidades = this.aghuFacade.pesquisarEspecialidadeLaudoAih(null, conNumero, mamLaudoAih.getPaciente().getIdade());
				if (!especialidades.isEmpty() && especialidades.size() > 0) {
					mamLaudoAih.setEspecialidade(especialidades.get(0));
				}
			}

		} else { // Se o SEQ do Laudo AIH recebido por parâmetro não for nulo, executa ON4.

			try {
				this.mamLaudoAih = this.ambulatorioFacade.obterLaudoAIHPorChavePrimaria(seq);
				// carrega SB Equipe
				if (mamLaudoAih.getServidorRespInternacao() != null) {
					List<Object> listaResp = this.blocoCirurgicoFacade.pesquisarVAinServInternaMatriculaVinculoEsp(mamLaudoAih.getServidorRespInternacao().getId().getMatricula(), mamLaudoAih
							.getServidorRespInternacao().getId().getVinCodigo(), mamLaudoAih.getEspecialidade().getSeq());
					List<VAinServInterna> bindedList = bind(listaResp);
					if (bindedList != null && !bindedList.isEmpty()) {
						this.setvAinServInterna(bindedList.get(0));
					}
				}

				// carrega SB Procedimento
				if (mamLaudoAih.getFatItemProcedHospital() != null) {

					List<VFatSsmInternacaoVO> listaProced = this.faturamentoFacade.buscarVFatSsmInternacaoPorIphPho(mamLaudoAih.getFatItemProcedHospital().getId().getPhoSeq(), mamLaudoAih
							.getFatItemProcedHospital().getId().getSeq());
					if (listaProced != null && !listaProced.isEmpty()) {
						this.setvFatSsmInternacaoVO(listaProced.get(0));
					}
				}

				// chama ON4
				this.ambulatorioFacade.protegerLiberarRegistroLai(mamLaudoAih.getSeq(), mamLaudoAih.getIndPendente(), mamLaudoAih.getServidorValida());

				this.blocoCirurgicoFacade.pesquisarVAinServInternaMatriculaVinculoEsp(mamLaudoAih.getServidorRespInternacao().getId().getMatricula(), mamLaudoAih.getServidorRespInternacao().getId()
						.getVinCodigo(), mamLaudoAih.getEspecialidade().getSeq());
				this.permiteEdicao = true;

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(EXCECAO_CAPTURADA, e);
				this.permiteEdicao = false;
			}

		}

		if (mamLaudoAih.getPaciente().getSexo() == null) {
			this.apresentarMsgNegocio(Severity.WARN, "VALIDA_SEXO_PREENCHIDO");
		}

	
	}
	

	public String gravar() {

		// Retorno da ON9, se gera ou não pendência de assinatura digital ao imprimir o relatório.
		Boolean assinaturaDigital = false;

		try {

			if (this.mamLaudoAih.getDtProvavelCirurgia() != null && DateUtil.validaDataMenor(this.mamLaudoAih.getDtProvavelCirurgia(), this.mamLaudoAih.getDtProvavelInternacao())) {
				this.apresentarMsgNegocio(Severity.ERROR, "VALIDA_CIRURGIA_APOS_INTERNACAO");
				return null;
			}

			if (this.mamLaudoAih.getSinaisSintomas().equalsIgnoreCase(this.mamLaudoAih.getCondicoes()) || this.mamLaudoAih.getSinaisSintomas().equalsIgnoreCase(this.mamLaudoAih.getResultadosProvas())
					|| this.mamLaudoAih.getCondicoes().equalsIgnoreCase(this.mamLaudoAih.getResultadosProvas())) {
				this.apresentarMsgNegocio(Severity.ERROR, "VALIDA_TEXTAREA_REPETIDO");
				return null;
			}
			


			this.mamLaudoAih.setAghCid(this.aghuFacade.obterAghCidPorChavePrimaria(this.mamLaudoAih.getAghCid().getSeq()));
			AghCid cid = this.mamLaudoAih.getAghCid();
			
			if (this.mamLaudoAih.getAghCidSecundario() != null) {
				this.mamLaudoAih.setAghCidSecundario(this.aghuFacade.obterAghCidPorChavePrimaria(this.mamLaudoAih.getAghCidSecundario().getSeq()));
			}else{
				if(cid.getCidInicialSecundario() != null && cid.getCidFinalSecundario() != null){
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.CID_PRINCIPAL_EXIGE_SECUNDARIO,
							cid.getCidInicialSecundario(), cid.getCidFinalSecundario());
				}
			}
			
//			String cidInicialSecundario = null;
//			String cidFinalSecundario = null;
//			if (cid.getCidInicialSecundario() != null) {
//				cidInicialSecundario = cid.getCid()
//						.getCidInicialSecundario();
//			}
//			if (cid.getCidFinalSecundario() != null) {
//				cidFinalSecundario = cid.getCidFinalSecundario();
//			}
//			
//			if (cidInicialSecundario != null && cidFinalSecundario != null){
//				if ((this.mamLaudoAih.getAghCidSecundario() == null)
//						|| (this.mamLaudoAih.getAghCidSecundario().getCodigo().compareTo(cidInicialSecundario) < 0 || this.mamLaudoAih.getAghCidSecundario().getCodigo()
//								.compareTo(cidFinalSecundario) > 0)) {
//					throw new ApplicationBusinessException(
//							CadastroInternacaoONExceptionCode.CID_PRINCIPAL_EXIGE_SECUNDARIO,
//							cidInicialSecundario, cidFinalSecundario);
//				}
//			}
			
			
			
			if (DominioNaturezaFichaAnestesia.ELE.equals(prioridade)) {
				this.mamLaudoAih.setPrioridade(Short.valueOf("1"));
			} else if(DominioNaturezaFichaAnestesia.URG.equals(prioridade)) {
				this.mamLaudoAih.setPrioridade(Short.valueOf("3"));
			}
			
			this.mamLaudoAih.setEspecialidade(this.aghuFacade.obterEspecialidadePorChavePrimaria(this.mamLaudoAih.getEspecialidade().getSeq()));

			if (mamLaudoAih.getSeq() == null) {
				// Em caso de novo registro executar ON1.
				assinaturaDigital = this.ambulatorioFacade.salvarMamLaudoAih(mamLaudoAih, mamLaudoAih.getPaciente());
				// this.ambulatorioFacade.flush();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_LAUDO_AIH");

			} else {
				// Em caso de edição executar ON2.
				assinaturaDigital = this.ambulatorioFacade.atualizarMamLaudoAih(mamLaudoAih, mamLaudoAih.getPaciente());
				// this.ambulatorioFacade.flush();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_LAUDO_AIH");
			}
			// this.ambulatorioFacade.flush();
			this.imprimirLaudoAih(assinaturaDigital);
			return voltarPara;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
			return null;
		} catch (JRException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			return null;
		} catch (SystemException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			return null;
		} catch (IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			return null;
		}

	}

	private void imprimirLaudoAih(Boolean assinaturaDigital) throws BaseException, JRException, SystemException, IOException {

		relatorioLaudoAIHController.setSeq(mamLaudoAih.getSeq());
		relatorioLaudoAIHController.setCodigoPac(mamLaudoAih.getPaciente().getCodigo());
		if (mamLaudoAih.getServidorValida() != null) {
			relatorioLaudoAIHController.setMatricula(mamLaudoAih.getServidorValida().getId().getMatricula());
			relatorioLaudoAIHController.setVinCodigo(mamLaudoAih.getServidorValida().getId().getVinCodigo());
		} else {
			RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorPorUsuario(this.obterLoginUsuarioLogado());
			relatorioLaudoAIHController.setMatricula(servidorLogado.getId().getMatricula());
			relatorioLaudoAIHController.setVinCodigo(servidorLogado.getId().getVinCodigo());
		}

		if (assinaturaDigital == true) {
			// verifica se deve imprimir laudo mesmo com certificação digital
			AghParametros paramImprimeLaudoAih = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPRIME_LAUDO_AIH);
			if (paramImprimeLaudoAih.getVlrTexto().equalsIgnoreCase("S")) {

				relatorioLaudoAIHController.directPrint(Boolean.TRUE);
			}

			// Gera pendência de Assinatura Digital
			relatorioLaudoAIHController.geraPendenciasCertificacaoDigital();

		} else if (assinaturaDigital == false) {

			relatorioLaudoAIHController.directPrint(Boolean.TRUE);
			return;

		}
	}

	public String voltar() {

		return this.voltarPara;
	}

	/*
	 * MÉTODOS DE BUSCA DAS SUGGESTION BOXES
	 */
	public List<AghEspecialidades> pesquisarEspecialidades(String param) {

		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadeLaudoAih(param, null, DateUtil.obterQtdAnosEntreDuasDatas(mamLaudoAih.getPaciente().getDtNascimento(), new Date())),pesquisarEspecialidadesCount(param));
	}

	public Integer pesquisarEspecialidadesCount(String param) {
		return this.aghuFacade.pesquisarEspecialidadeLaudoAih(param, null, DateUtil.obterQtdAnosEntreDuasDatas(mamLaudoAih.getPaciente().getDtNascimento(), new Date())).size();
	}

	public void posRemoverEspecialidade() {
		this.vAinServInterna = null;
		this.mamLaudoAih.setEspecialidade(null);
	}

	public List<VAinServInterna> pesquisarEquipe(String param) {
		List<Object> lista = this.blocoCirurgicoFacade.pesquisarVAinServInternaLaudoAih(param, mamLaudoAih.getEspecialidade().getSeq());
		return this.returnSGWithCount(this.bind(lista),pesquisarEquipeCount(param));
	}

	private List<VAinServInterna> bind(List<Object> objList) {
		List<VAinServInterna> lista = new ArrayList<VAinServInterna>();
		for (Object object : objList) {
			VAinServInterna binded = new VAinServInterna();
			binded.setId(new VAinServInternaId());
			binded.getId().setNome((String) ((Object[]) object)[0]);
			binded.getId().setNroRegConselho((String) ((Object[]) object)[1]);
			binded.getId().setMatricula((Integer) ((Object[]) object)[2]);
			binded.getId().setVinCodigo((Short) ((Object[]) object)[3]);
			lista.add(binded);
		}

		return lista;
	}

	public Long pesquisarEquipeCount(String param) {
		return this.blocoCirurgicoFacade.pesquisarVAinServInternaLaudoAihCount(param, mamLaudoAih.getEspecialidade().getSeq());
	}

	public void posSelecionarEquipe() {
		RapServidoresId id = new RapServidoresId(this.vAinServInterna.getId().getMatricula(), this.vAinServInterna.getId().getVinCodigo());
		RapServidores respInternacao = this.registroColaboradorFacade.obterRapServidor(id);
		this.mamLaudoAih.setServidorRespInternacao(respInternacao);
	}

	public List<VFatSsmInternacaoVO> pesquisarProcedimentos(String param) {
		try {
			return this.returnSGWithCount(this.ambulatorioFacade.obterListaProcedimentosLaudoAih(param, mamLaudoAih.getPaciente(), (mamLaudoAih.getAghCid() == null ? null : mamLaudoAih.getAghCid().getSeq())),pesquisarProcedimentosCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public Long pesquisarProcedimentosCount(String param) {
		try {
			return this.ambulatorioFacade.obterListaProcedimentosLaudoAihCount(param, mamLaudoAih.getPaciente(), (mamLaudoAih.getAghCid() == null ? null : mamLaudoAih.getAghCid().getSeq()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public void posSelecionarProcedimento() {
		FatItensProcedHospitalar itemProcedHospitalar = this.faturamentoFacade.obterItemProcedimentoHospitalar(this.vFatSsmInternacaoVO.getIphSeq(), this.vFatSsmInternacaoVO.getPhoSeq());
		this.mamLaudoAih.setFatItemProcedHospital(itemProcedHospitalar);
	}

	public List<AghCid> pesquisarCids(String param) {
		try {
			return this.returnSGWithCount(this.ambulatorioFacade.obterListaCidLaudoAih(param, mamLaudoAih.getPaciente(), (vFatSsmInternacaoVO == null ? null : vFatSsmInternacaoVO.getCodTabela())),pesquisarCidsCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public Long pesquisarCidsCount(String param) {
		try {
			return this.ambulatorioFacade.obterListaCidLaudoAihCount(param, mamLaudoAih.getPaciente(), (vFatSsmInternacaoVO == null ? null : vFatSsmInternacaoVO.getCodTabela()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public void posSelecionarCid() {
		AghCid cidSecundario = this.aghuFacade.obterCidPermiteCidSecundario(mamLaudoAih.getAghCid().getSeq());
		if (cidSecundario != null) {
			habilitaCidSecundario = true;
		}
	}

	public void posRemoverCid() {
		this.mamLaudoAih.setAghCid(null);
		this.mamLaudoAih.setAghCidSecundario(null);
		this.habilitaCidSecundario = false;
	}

	public List<AghCid> pesquisarCidsSecundario(String param) {
		try {
			return this.returnSGWithCount(this.ambulatorioFacade.obterListaCidSecundarioLaudoAih(param, mamLaudoAih.getPaciente(), mamLaudoAih.getAghCid().getCodigo()),pesquisarCidsCountSecundario(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public Long pesquisarCidsCountSecundario(String param) {
		try {
			return this.ambulatorioFacade.obterListaCidSecundarioLaudoAihCount(param, mamLaudoAih.getPaciente(), mamLaudoAih.getAghCid().getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	public void posRemoverCidSecundario() {
		this.mamLaudoAih.setAghCidSecundario(null);
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Long getRgtSeq() {
		return rgtSeq;
	}

	public void setRgtSeq(Long rgtSeq) {
		this.rgtSeq = rgtSeq;
	}

	public DominioSimNao getAssinarLaudo() {
		return assinarLaudo;
	}

	public void setAssinarLaudo(DominioSimNao assinarLaudo) {
		this.assinarLaudo = assinarLaudo;
	}

	public MamLaudoAih getMamLaudoAih() {
		return mamLaudoAih;
	}

	public void setMamLaudoAih(MamLaudoAih mamLaudoAih) {
		this.mamLaudoAih = mamLaudoAih;
	}

	public DominioNaturezaFichaAnestesia[] getItensPrioridade() {
		return itensPrioridade;
	}

	public void setItensPrioridade(DominioNaturezaFichaAnestesia[] itensPrioridade) {
		this.itensPrioridade = itensPrioridade;
	}

	public DominioNaturezaFichaAnestesia getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(DominioNaturezaFichaAnestesia prioridade) {
		this.prioridade = prioridade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public VAinServInterna getvAinServInterna() {
		return vAinServInterna;
	}

	public void setvAinServInterna(VAinServInterna vAinServInterna) {
		this.vAinServInterna = vAinServInterna;
	}

	public VFatSsmInternacaoVO getvFatSsmInternacaoVO() {
		return vFatSsmInternacaoVO;
	}

	public void setvFatSsmInternacaoVO(VFatSsmInternacaoVO vFatSsmInternacaoVO) {
		this.vFatSsmInternacaoVO = vFatSsmInternacaoVO;
	}

	public boolean isHabilitaCidSecundario() {
		return habilitaCidSecundario;
	}

	public void setHabilitaCidSecundario(boolean habilitaCidSecundario) {
		this.habilitaCidSecundario = habilitaCidSecundario;
	}

	public String getOrigemAmbulatorio() {
		return origemAmbulatorio;
	}

	public void setOrigemAmbulatorio(String origemAmbulatorio) {
		this.origemAmbulatorio = origemAmbulatorio;
	}

	public boolean isPermiteEdicao() {
		return permiteEdicao;
	}

	public void setPermiteEdicao(boolean permiteEdicao) {
		this.permiteEdicao = permiteEdicao;
	}
}
