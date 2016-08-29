package br.gov.mec.aghu.blococirurgico.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioStatusRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipControleEscalaCirurgia;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório Escala Cirurgias.
 * 
 * @author lalegre
 * 
 */
@Stateless
public class EscalaCirurgiasON extends BaseBusiness {
private static final String PRONTUARIOS_A_MOVIMENTAR_EM = "- Prontuários a movimentar em ";

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EscalaCirurgiasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7930005962266244729L;
	private static final String STRING_SEPARATOR = ".";

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais e pesquisa
	 * do relatório Escala Cirurgias.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum EscalaCirurgiasONExceptionCode implements BusinessExceptionCode {
		STATUS_INVALIDO, UNIDADE_SEM_PARAMETRO
	}
	
	

	/**
	 * Método que retorna lista EscalaCirurgiasVO para a geração do relatório.
	 * @param unidadesFuncionais
	 * @param dataCirurgia
	 * @param status
	 * @param servidorLogado
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<EscalaCirurgiasVO> pesquisa(AghUnidadesFuncionais unidadesFuncionais, Date dataCirurgia, DominioStatusRelatorio status) throws ApplicationBusinessException {
		
		EscalaCirurgiasVO cirurgiaVO = validarEscalaCirurgica(unidadesFuncionais, dataCirurgia, status);
		
		List<Object[]> res = this.getMbcCirurgiasDAO().pesquisaInformacoesEscalaCirurgias(unidadesFuncionais.getSeq(), dataCirurgia, cirurgiaVO.getDataInicial(), cirurgiaVO.getDataFinal());

		List<EscalaCirurgiasVO> lista = new ArrayList<EscalaCirurgiasVO>();
		SimpleDateFormat hora = new SimpleDateFormat("HH:mm");
		SimpleDateFormat ano = new SimpleDateFormat("yyyy");
		Date dataAtual = new Date();
		int totalCirurgias = 0;

		// Criando lista de VO.
		Iterator<Object[]> it = res.iterator();
		
		while (it.hasNext()) {
		
			Object[] obj = it.next();
			EscalaCirurgiasVO vo = new EscalaCirurgiasVO();
			totalCirurgias = totalCirurgias + 1;

			if (obj[0] != null) {
				vo.setSciSeqp(((Short) obj[0]).toString());
			}
			if (obj[1] != null) {
				vo.setDthrInicioCirg((hora.format((Date) obj[1])));
			}
			if (obj[2] != null) {
				vo.setCspCnvCodigo(((Short) obj[2]).toString());
			}
			if (obj[3] != null) {
				vo.setNome((String) obj[3]);
			}
			if (obj[4] != null) {
				int anoAtual = Integer.valueOf(ano.format(dataAtual));
				int anoNascimento = Integer.valueOf(ano.format((Date) obj[4]));
				vo.setDtNascimento(String.valueOf(anoAtual - anoNascimento));
			}
			if (obj[5] != null) {
				DominioOrigemPacienteCirurgia dom = (DominioOrigemPacienteCirurgia) obj[5];
				vo.setOrigemPacCirg(dom.getDescricao().substring(0, 1));
			}
			if (obj[6] != null) {
				String prontAux = ((Integer) obj[6]).toString();
				vo.setProntuario1(prontAux);
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}
			
			if (obj[7] != null && (DominioTipoProntuario) obj[7] == DominioTipoProntuario.P) { 
				
				vo.setPrntAtivo("*");
			
			}
			
			if (obj[8] != null) {
			
				Boolean indPrecaucao = (Boolean) obj[8];
				
				if (indPrecaucao) {
					
					vo.setIndPrecaucaoEspecial("SIM");
					
				} else {
					
					vo.setIndPrecaucaoEspecial("NÃO");
				
				}
			
			}
			
			if (obj[9] != null) {

				Integer seq = (Integer) obj[9];
				vo.setSeq(seq.toString());

				// Tipo Anestesia
				vo.setTipoAnestesia(this.pesquisaTipoAnestesia(seq));

				vo.setNomeCirurgiao(obterCirurgiao(seq));
			
				// Lista Procedimento Cirurgico
				List<String> procedimento = this.pesquisaProcedimentoCirurgico(seq); 
				StringBuffer procedimentoCirurgico = null;
				
				for (String proc : procedimento) {
					
					if (procedimentoCirurgico != null) {
						
						procedimentoCirurgico.append("                                 ").append(proc);
						totalCirurgias = totalCirurgias + 1;
						
					} else {
						
						procedimentoCirurgico = new StringBuffer(proc);
						
					}
					
				}
				
				if (procedimentoCirurgico != null) {
					
					vo.setProcedimentoCirurgico(procedimentoCirurgico.toString().replace("null", ""));
					
				}
				
			}
			
			if (obj[10] != null) {
				
				vo.setPacCodigo(((Integer) obj[10]).toString());
				// Pesquisa por quarto/leito
				vo.setQuarto(this.pesquisaQuarto((Integer) obj[10]));
				
			}
			
			if (obj[11] != null) {
				
				vo.setVolumes(((Short) obj[11]).toString());
				
			}
			
			if (obj[12] != null) {
				
				vo.setCriadoEm(((Date) obj[12]).toString());
				
			}

			lista.add(vo);
		}

		// Ordenação por seção do prontuário.
		// substr(to_char(PAC.PRONTUARIO,'09999999'),7,2)
		// Ex: Para o prontuário 123401/5 o que deve ser considerado para
		// ordenação é o número 01.
		Collections.sort(lista, new CirurgiasProntuarioComparator());

		// Seta o total de cirurgias no último elemento da lista
		if (lista.size() > 0) {
			
			lista.get(lista.size() - 1).setTotalCirurgias("TOTAL DE CIRURGIAS: " + totalCirurgias);
			
		} else {
			
			EscalaCirurgiasVO vo = new EscalaCirurgiasVO();
			vo.setTotalCirurgias("TOTAL DE CIRURGIAS: 0");
			lista.add(new EscalaCirurgiasVO());
			lista.add(vo);
			
		}
		
		lista.get(lista.size() - 1).setTitulo(escalaFormula(status, dataCirurgia, cirurgiaVO.getDataInicial(), cirurgiaVO.getDataFinal()));

		return lista;
	}
	
	
	public String obterCirurgiao(Integer seq) {
		StringBuffer nomeCirurgiao = null;
		List<Object[]> cirurgiao = this.pesquisaCirurgiao(seq);
		
		for (Object[] crg : cirurgiao) {
			
			if ((String) crg[1] != null && nomeCirurgiao != null) {
				
				nomeCirurgiao.append("      ").append((String) crg[1]); // NOME USUAL
				
			} else if ((String) crg[1] == null && nomeCirurgiao != null) {
				
				nomeCirurgiao.append("      ").append(((String) crg[0]).substring(0, 13)); // NOME
				
			} else if ((String) crg[1] != null && nomeCirurgiao == null) {
				
				nomeCirurgiao = new StringBuffer((String) crg[1]); // NOME USUAL
				
			} else {
				
				nomeCirurgiao = new StringBuffer(((String) crg[0])); // NOME
				
			}
			
		}
		
		return nomeCirurgiao!=null?nomeCirurgiao.toString():null;
	}
	
	/**
	 * RN da Escala de Cirurgia do SAMIS, versao AGHU
	 * @param unidade
	 * @param dataCirurgia
	 * @param status
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private EscalaCirurgiasVO validarEscalaCirurgica(AghUnidadesFuncionais unidadesFuncionais, Date dataCirurgia, DominioStatusRelatorio status) throws ApplicationBusinessException {
		
		final IPacienteFacade pacienteFacade = getPacienteFacade();
		EscalaCirurgiasVO vo = new EscalaCirurgiasVO();
		
		verificarSolicitante(unidadesFuncionais.getSeq());
		
		if (!status.equals(DominioStatusRelatorio.R)) {
			
			if (pacienteFacade.existeEscalaDefinitiva(unidadesFuncionais.getSeq(), dataCirurgia)) {
				
				throw new ApplicationBusinessException(EscalaCirurgiasONExceptionCode.STATUS_INVALIDO);
				
			} 
			
			if (status.equals(DominioStatusRelatorio.D)) {
				
				vo.setDataFinal(new Date());
				inserirAipControleEscalaCirurgia(unidadesFuncionais, status, dataCirurgia, null, null);
				
			} else if (status.equals(DominioStatusRelatorio.P)) {
				
				vo.setDataInicial(pacienteFacade.recuperarMaxDataFim(unidadesFuncionais.getSeq(), dataCirurgia));
				vo.setDataFinal(new Date());
				inserirAipControleEscalaCirurgia(unidadesFuncionais, status, dataCirurgia, vo.getDataInicial(), vo.getDataFinal());
				
			}
			
		} else {
			
			vo.setDataFinal(new Date());
			
		}
		
		return vo;
		
	}
	
	/**
	 * Insere o controle de geracao do relatorio
	 * @param unidadesFuncionais
	 * @param status
	 * @param dataExec
	 * @param dataInicio
	 * @param dataFim
	 * @param servidor
	 */
	private void inserirAipControleEscalaCirurgia(AghUnidadesFuncionais unidadesFuncionais, DominioStatusRelatorio status, Date dataExec, Date dataInicio, Date dataFim) {
		AipControleEscalaCirurgia controleEscalaCirurgia = new AipControleEscalaCirurgia();
		controleEscalaCirurgia.setUnidadesFuncionais(unidadesFuncionais);
		controleEscalaCirurgia.setDataExec(dataExec);
		controleEscalaCirurgia.setServidor(servidorLogadoFacade.obterServidorLogado());
		controleEscalaCirurgia.setStatus(status);
		controleEscalaCirurgia.setDataInicio(dataInicio);
		controleEscalaCirurgia.setDataFim(dataFim);

		getPacienteFacade().persistirAipControleEscalaCirurgia(controleEscalaCirurgia);
		//getPacienteFacade().flush();
		
	}
	
	public String pesquisaQuarto(Integer pac_codigo) {
		return this.pesquisaQuarto(pac_codigo, true);
	}

	/**
	 * Método que retorna o Quarto/Leito de acordo com o codigo do paciente.
	 * ORADB function MBCC_LOCAL_AIP_PAC
	 * 
	 * @param pac_codigo
	 * @return
	 */
	public String pesquisaQuarto(Integer pac_codigo, boolean ativarLinhaRelatorio) {

		String retorno = null;
		
		if(ativarLinhaRelatorio){
			retorno = "_______";
		}

		if (pac_codigo != null) {
			List<Object[]> res = this.getPacienteFacade().pesquisaInformacoesPacienteEscalaCirurgias(pac_codigo);
			Iterator<Object[]> it = res.iterator();

			if (it.hasNext()) {
				Object[] obj = it.next();
				Date dataAlta = null;
				Date dataInternacao = null;
				if (obj[4] != null) {
					dataAlta = (Date) obj[4];
				}
				if (obj[3] != null) {
					dataInternacao = (Date) obj[3];
				}
				if (dataInternacao != null) {
					if ((dataAlta == null)
							|| (dataAlta != null && dataAlta
									.before(dataInternacao))) {
						if ((String) obj[0] != null) {
							retorno = "L:" + (String) obj[0];
						} else if ( obj[1] != null) {
							retorno = "Q:" + (String) obj[1];
						} else if ((Short) obj[2] != null) {
							List<Object[]> res2 = this.getAghuFacade()
									.pesquisaInformacoesUnidadesFuncionaisEscalaCirurgias((Short) obj[2]);
							Iterator<Object[]> it2 = res2.iterator();
							if (it2.hasNext()) {
								Object[] obj2 = it2.next();
								if(obj[0] != null && obj[1] != null){
									retorno = "U:" + ( obj2[0]).toString()
											+ " "
											+ ((AghAla) obj2[1]).toString();
								}
							}
						}
					}
				}
			}
		}
		return retorno;
	}
	
	public String pesquisaQuarto(CirurgiaVO vo, boolean ativarLinhaRelatorio) {
		return this.pesquisaQuarto(vo.getLtoLtoId(), vo.getQrtDescricao(), vo.getPacUnfSeq(), vo.getDtUltInternacao(), vo.getDtUltAlta(), vo.getPacUnfAndar(), vo.getPacUnfAla(), ativarLinhaRelatorio);
	}
	
	public String pesquisaQuarto(String ltoLtoId, String qrtDescricao, Short pacUnfSeq, Date dtUltInternacao, Date dtUltAlta, Byte pacUnfAndar, String pacUnfAla, boolean ativarLinhaRelatorio) {
		String retorno = null;
		
		if(ativarLinhaRelatorio){
			retorno = "_______";
		}

		Date dataAlta = null;
		Date dataInternacao = null;
		if (dtUltAlta != null) {
			dataAlta = dtUltAlta;
		}
		if (dtUltInternacao != null) {
			dataInternacao = dtUltInternacao;
		}
		if (dataInternacao != null) {
			if ((dataAlta == null)
					|| (dataAlta != null && dataAlta.before(dataInternacao))) {
				if (ltoLtoId != null) {
					retorno = "L:" + ltoLtoId;
				} else if (qrtDescricao != null) {
					retorno = "Q:" + qrtDescricao;
				} else if (pacUnfSeq != null) {
					retorno = "U:" + pacUnfAndar.toString() + " " + pacUnfAla;
				}
			}
		}

		return retorno;
	}
	
	/**
	 * Retorna uma lista de procedimentos cirurgicos através do sequence de
	 * MbcCirurgias
	 * 
	 * @param seq
	 * @return
	 */
	public List<String> pesquisaProcedimentoCirurgico(Integer seq) {
		return this.getMbcProcEspPorCirurgiasDAO().pesquisaProcedimentoCirurgico(seq, STRING_SEPARATOR,
				 this.cfNotaFormula(seq)
		);
	}

	
	public List<MbcProcEspPorCirurgias> pesquisaProcedimentoCompleto(Integer seq) {
		return this.getMbcProcEspPorCirurgiasDAO().pesquisaProcedimentoCompleto(seq, STRING_SEPARATOR,
				 this.cfNotaFormula(seq)
		);
	}
	/**
	 * TODO Alterar nome do metodo para pesquisarNotaFormula ORADB function
	 * CF_NOTAFormula
	 * 
	 * @param seq
	 * @return
	 */
	public DominioIndRespProc cfNotaFormula(Integer seq) {
		DominioIndRespProc dom = DominioIndRespProc.AGND;

		List<DominioIndRespProc> res = this.getMbcProcEspPorCirurgiasDAO().pesquisarStatusResponsabilidadeProcedimentosCirurgicos(seq,
				STRING_SEPARATOR);
		Iterator<DominioIndRespProc> it = res.iterator();
		if (it.hasNext()) {
			dom = it.next();
		}
		return dom;
	}

	/**
	 * Pesquisa o tipo de anestesia através do sequence de MbcCirurgias
	 * 
	 * @param seq
	 * @return
	 */
	public String pesquisaTipoAnestesia(Integer seq) {
		String tipoAnestesia = "";
		
		List<String> res = this.getMbcAnestesiaCirurgiasDAO().pesquisaTiposAnestesia(seq, STRING_SEPARATOR);
		
		Iterator<String> it = res.iterator();
		if (it.hasNext()) {
			tipoAnestesia = it.next();
		}
		return tipoAnestesia;
	}
	
	
	/**
	 * 
	 * @param unfSeq
	 * @throws ApplicationBusinessException
	 */
	private void verificarSolicitante(Short unfSeq) throws ApplicationBusinessException {
		
		Long count = this.getPacienteFacade().countSolicitantesPorUnidadeFuncional(unfSeq);
		
		if (count < 1) {
		
			throw new ApplicationBusinessException(EscalaCirurgiasONExceptionCode.UNIDADE_SEM_PARAMETRO);
		
		}
	
	}
	
	
	/**
	 * ORADB function CF_ESCALAFormula Define a escala do relatório.
	 * 
	 * @return escala
	 */
	private String escalaFormula(DominioStatusRelatorio status, Date dataCirurgia, Date dataIni, Date dataSup) {
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
		String descricao = "";

		if (status == DominioStatusRelatorio.P) {
			
			if (dataIni == null) {
				
				descricao = PRONTUARIOS_A_MOVIMENTAR_EM + sdf2.format(dataCirurgia) + " - Emissão parcial até " + sdf.format(dataSup);
			
			} else {

				descricao = PRONTUARIOS_A_MOVIMENTAR_EM + sdf2.format(dataCirurgia) + " - Emissão parcial em " + sdf.format(dataIni) + " até " + sdf3.format(dataSup);
		
			}
			
		} else if (status == DominioStatusRelatorio.R) {
			
			if (dataIni == null) {
				
				descricao = PRONTUARIOS_A_MOVIMENTAR_EM + sdf2.format(dataCirurgia)
						+ " - Re-emissão parcial até " + sdf.format(dataSup);
				
			} else {
				
				descricao = PRONTUARIOS_A_MOVIMENTAR_EM + sdf2.format(dataCirurgia)
						+ " - Re-emissão parcial em " + sdf.format(dataIni) + " até "
						+ sdf3.format(dataSup);
			}
			
		} else {
			
			descricao = PRONTUARIOS_A_MOVIMENTAR_EM + sdf2.format(dataCirurgia) + " - Emissão definitiva em " + sdf.format(dataSup);
		
		}

		return descricao;
		
	}
	

	/**
	 * Pesquisa cirurgiao através do sequence de MbcCirurgias
	 * 
	 * @param seq
	 * @return
	 */
	public List<Object[]> pesquisaCirurgiao(Integer seq) {
		return this.getMbcProfCirurgiasDAO().pesquisaCirurgiao(seq, STRING_SEPARATOR);
	}


	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	
}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>EscalaCirurgiasVO</code> pela seção do prontuário, que é definida por
 * dois dígitos no numero do prontuário. Ex: Um prontuário como número 123401/5
 * possui o número do seção 01, representado pelo 5 e 6 dígito. E os itens tem
 * vir ordenados por esse campo.
 * 
 * @author lalegre
 * 
 */
class CirurgiasProntuarioComparator implements Comparator<EscalaCirurgiasVO> {

	@Override
	public int compare(EscalaCirurgiasVO o1, EscalaCirurgiasVO o2) {

		// Exemplo: o1 = 388208/3 e o2 = 233208/8
		String vo1Desc = ((EscalaCirurgiasVO) o1).getProntuario();
		String vo2Desc = ((EscalaCirurgiasVO) o2).getProntuario();

		// Exemplo: secao1 = 08 e secao2 = 08
		int secao1 = Integer.parseInt(vo1Desc.substring(vo1Desc.length() - 4,
				vo1Desc.length() - 2));

		int secao2 = Integer.parseInt(vo2Desc.substring(vo2Desc.length() - 4,
				vo2Desc.length() - 2));

		int pre1 = 0;
		int pre2 = 0;

		// Exemplo: secao1 = 3882 e secao2 = 2332
		if (vo1Desc.length() > 4) {
			pre1 = Integer.parseInt(vo1Desc.substring(0, vo1Desc.length() - 4));
		}

		if (vo2Desc.length() > 4) {
			pre2 = Integer.parseInt(vo2Desc.substring(0, vo2Desc.length() - 4));
		}

		if (secao1 > secao2) {
			return 1;
		} else if (secao1 < secao2) {
			return -1;
		} else {
			if (pre1 > pre2) {
				return 1;
			} else if (pre1 < pre2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	
}
