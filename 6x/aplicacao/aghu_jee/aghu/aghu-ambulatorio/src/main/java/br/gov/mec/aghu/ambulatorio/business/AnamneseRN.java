package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamFiguraAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamImagemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespQuestAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpAlturasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaDiastolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaSistolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPerimCefalicosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPesosDAO;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseAutorelacaoVO;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.GeraAnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.RespostaAnamneseVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamFiguraAnamnese;
import br.gov.mec.aghu.model.MamImagemAnamnese;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemAnamnesesId;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamRespQuestAnamneses;
import br.gov.mec.aghu.model.MamRespQuestAnamnesesId;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamRespostaAnamnesesId;
import br.gov.mec.aghu.model.MamTmpAlturas;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;
import br.gov.mec.aghu.model.MamTmpPerimCefalicos;
import br.gov.mec.aghu.model.MamTmpPesos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por manter as regras de negócio da Anamnese.
 * 
 * @author rafael.silvestre
 */
@Stateless
public class AnamneseRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4836959402360101754L;
	
	private static final Log LOG = LogFactory.getLog(AnamneseRN.class);

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamRespostaAnamnesesDAO mamRespostaAnamnesesDAO;
	
	@Inject
	private MamFiguraAnamnesesDAO mamFiguraAnamnesesDAO;

	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;
	
	@Inject
	private MamRespQuestAnamnesesDAO mamRespQuestAnamnesesDAO;
	
	@Inject
	private MamImagemAnamnesesDAO mamImagemAnamnesesDAO;

	@Inject
	private MamTmpPesosDAO mamTmpPesosDAO;
	
	@Inject
	private MamTmpAlturasDAO mamTmpAlturasDAO;
	
	@Inject
	private MamTmpPerimCefalicosDAO mamTmpPerimCefalicosDAO;
	
	@Inject
	private MamTmpPaSistolicasDAO mamTmpPaSistolicasDAO;
	
	@Inject
	private MamTmpPaDiastolicasDAO mamTmpPaDiastolicasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade; 

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private MamRespostaAnamnesesRN mamRespostaAnamnesesRN;
	
	@EJB
	private CancelamentoAtendimentoRN cancelamentoAtendimentoRN;
	
	@EJB
	private EvolucaoON evolucaoON;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public enum AnamneseRNExceptionCode implements BusinessExceptionCode {
		MAM_00948, MENSAGEM_VALIDACAO_DESCRICAO_ITEM_ANAMNESE
    }
	
	/**
	 * #50745 - Método invocado pelos botões Gravar e Ok da tela de Anamnese.
	 */
	public void gravarAnamnese(Integer conNumero, Long anaSeq, List<AnamneseVO> listaBotoes) throws ApplicationBusinessException {
		
		GeraAnamneseVO geraAnamneseVO = geraAnamnese(conNumero, anaSeq);
		
		if (listaBotoes != null && !listaBotoes.isEmpty()) {
			for (AnamneseVO item : listaBotoes) {
				if (StringUtils.isNotBlank(item.getTexto())) {
					if (item.getTexto().length() > 4000) {
						throw new ApplicationBusinessException(AnamneseRNExceptionCode.MENSAGEM_VALIDACAO_DESCRICAO_ITEM_ANAMNESE);
					}
					MamItemAnamnesesId id = new MamItemAnamnesesId();
					id.setAnaSeq(geraAnamneseVO.getAnaSeq());
					id.setTinSeq(item.getTipoItemAnamnese().getSeq());
					MamItemAnamneses itemAnamnese = mamItemAnamnesesDAO.obterPorChavePrimaria(id);
					// caso não exista o item de anamnese
					Boolean insere = false;
					if (itemAnamnese == null) {
						itemAnamnese = new MamItemAnamneses();
						itemAnamnese.setId(id);
						insere = true;
					}
					// Atualizar o conteúdo da tabela de itens da anamnese com os valores informados nas abas livres de cada botão
					String textoAjustado = item.getTexto().replaceAll("\\r\\n", "\n");
					itemAnamnese.setDescricao(textoAjustado);
					if (insere) {
						mamItemAnamnesesDAO.persistir(itemAnamnese);
					} else {
						mamItemAnamnesesDAO.merge(itemAnamnese);
					}
				} else {
					MamItemAnamnesesId id = new MamItemAnamnesesId();
					id.setAnaSeq(geraAnamneseVO.getAnaSeq());
					id.setTinSeq(item.getTipoItemAnamnese().getSeq());
					MamItemAnamneses itemAnamnese = mamItemAnamnesesDAO.obterPorChavePrimaria(id);
					if (itemAnamnese != null) {
						mamItemAnamnesesDAO.remover(itemAnamnese);
					}
				}
			}
			mamItemAnamnesesDAO.flush();
		}
		
		// se p1 for 'S' executa U1 e U2
		if ("S".equals(verUsuarioFezAnamnese(geraAnamneseVO.getAnaSeq()))) {
			// a) Atualizar a tabela da anamnese (U1)
			MamAnamneses anamnese = mamAnamnesesDAO.obterAnamnesePorSeqEIndPendente(geraAnamneseVO.getAnaSeq(), DominioIndPendenteAmbulatorio.R);
			if (anamnese != null) {
				anamnese.setPendente(DominioIndPendenteAmbulatorio.P);
				mamAnamnesesDAO.atualizar(anamnese);
			}
			// b) Atualizar a tabela da anamnese (U2)
			if (geraAnamneseVO.getAnaAnaSeq() != null) {
				MamAnamneses anamnese2 = mamAnamnesesDAO.obterAnamnesePorSeqEIndPendente(geraAnamneseVO.getAnaAnaSeq(), DominioIndPendenteAmbulatorio.V);
				if (anamnese2 != null) {
					anamnese2.setPendente(DominioIndPendenteAmbulatorio.A);
					anamnese2.setServidorMvto(servidorLogadoFacade.obterServidorLogado());
					mamAnamnesesDAO.atualizar(anamnese2);
				}
			}
		}
	}
	
	/**
	 * #50745 - P1 - Verifica se o usuário logado realizou uma anamnese.
	 *  
	 * @ORADB MAMC_VER_USR_FEZ_ANA
	 */
	public String verUsuarioFezAnamnese(Long pAnaSeq) {
		
		DominioIndPendenteAmbulatorio vIndPendente = null;
		Long vAnaAnaSeq = null;
		Integer vPacCodigo = null;
		Integer vConNumero = null;
		String vRespostaCadastral = null;
		
		String vUsrFezAna = "N";
		String vTemAna = "N";
		String vTemItemAna = "N";
		String vTemRespostaAna = "N";
		String vTemFiguraAna = "N";
		String vTemNotaAna = "N";
		
		// verifica o registro da anamnese
		MamAnamneses  curAna = mamAnamnesesDAO.obterPorChavePrimaria(pAnaSeq);
		if (curAna != null) {
			vIndPendente = curAna.getPendente();
			vAnaAnaSeq = verificaAna(curAna,vAnaAnaSeq);
			vPacCodigo = curAna.getPaciente().getCodigo();
			if (curAna.getConsulta() != null) {
				vConNumero = curAna.getConsulta().getNumero();
			}
		}
		
		vTemAna = verificaTemAna(vIndPendente, vAnaAnaSeq, vTemAna);
		
		if ("N".equals(vTemAna)) {
			// verifica o item de anamnese
			vTemItemAna = cursorItemAnamnese(pAnaSeq, vTemItemAna);
			
			if ("N".equals(vTemItemAna)) {
				// verifica as respostas de anamneses
				vTemRespostaAna = "N";
				List<RespostaAnamneseVO> curRea = mamRespostaAnamnesesDAO.obterRespostaAnamnesePorAnaSeq(pAnaSeq);	
				for (RespostaAnamneseVO respostaAnamneseVO : curRea) {
					if (respostaAnamneseVO.getVvqQusQutSeq() != null || respostaAnamneseVO.getVvqQusSeqp() != null || respostaAnamneseVO.getVvqSeqp() != null) {
						vTemRespostaAna = "S";
					}
					if (respostaAnamneseVO.getFueSeq() == null) {
						vTemRespostaAna = verificaTemRespostaAna(vTemRespostaAna, respostaAnamneseVO);
					} else {
						vRespostaCadastral = evolucaoON.mamcExecFncEdicao(respostaAnamneseVO.getFueSeq(), vPacCodigo);
						vTemRespostaAna = verificaTemRespostaAnaSemSeq(vTemRespostaAna, respostaAnamneseVO, vRespostaCadastral);
					}
				 }
				 if ("N".equals(vTemRespostaAna)) {
					 //  verifica o item de anamnese
					 vTemFiguraAna = cursorFiguraAnamnese(vConNumero, vTemFiguraAna);
					 
					 if ("N".equals(vTemFiguraAna)) {
						// verifica a nota adicional de anamnese
						vTemNotaAna = cursorNotaAdicionalAnamnese(vConNumero, vTemNotaAna);
					 }
				 }
			}
		}
		
		if ("S".equals(vTemAna) || "S".equals(vTemItemAna) || "S".equals(vTemRespostaAna) || "S".equals(vTemFiguraAna) || "S".equals(vTemNotaAna)) {
			vUsrFezAna = "S";
		} else {
			vUsrFezAna = "N";
		}
		
		return vUsrFezAna;
	}

	private Long verificaAna(MamAnamneses curAna, Long vAnaAnaSeq) {
		
		if (curAna.getAnamnese() != null) {
			vAnaAnaSeq = curAna.getAnamnese().getSeq();
		}
		return vAnaAnaSeq;
	}

	private String verificaTemAna(DominioIndPendenteAmbulatorio vIndPendente, Long vAnaAnaSeq, String vTemAna) {
		
		if (DominioIndPendenteAmbulatorio.R.equals(vIndPendente)) {
			vTemAna = "N";
		} else if (DominioIndPendenteAmbulatorio.P.equals(vIndPendente) && vAnaAnaSeq == null) {
			vTemAna = "N";
		} else {
			vTemAna = "S";
		}
		return vTemAna;
	}

	private String cursorItemAnamnese(Long pAnaSeq, String vTemItemAna) {
		
		Long curIan	= mamItemAnamnesesDAO.obterCountItemAnamnesePorAnaSeq(pAnaSeq);
		
		if (curIan > 0) {
			vTemItemAna = "S";
		} else {
			vTemItemAna = "N";
		}
		return vTemItemAna;
	}

	private String verificaTemRespostaAna(String vTemRespostaAna, RespostaAnamneseVO respostaAnamneseVO) {
		
		if (respostaAnamneseVO.getTextoFormatado() == null && respostaAnamneseVO.getResposta() != null) {
			vTemRespostaAna = "S";
		} else if (respostaAnamneseVO.getTextoFormatado() != null && respostaAnamneseVO.getResposta() == null) {
			vTemRespostaAna = "S";
		} else if (respostaAnamneseVO.getTextoFormatado() != null && respostaAnamneseVO.getResposta() != null
				&& !respostaAnamneseVO.getTextoFormatado().equals(respostaAnamneseVO.getResposta())) {
			vTemRespostaAna = "S";
		}
		return vTemRespostaAna;
	}

	private String verificaTemRespostaAnaSemSeq(String vTemRespostaAna, RespostaAnamneseVO respostaAnamneseVO, String vRespostaCadastral) {
		
		if (vRespostaCadastral == null && respostaAnamneseVO.getResposta() != null) {
			vTemRespostaAna = "S";
		}
		if (vRespostaCadastral != null && respostaAnamneseVO.getResposta() == null) {
			vTemRespostaAna = "S";
		}
		if (vRespostaCadastral != null && respostaAnamneseVO.getResposta() != null && !vRespostaCadastral.equals(respostaAnamneseVO.getResposta())) {
			vTemRespostaAna = "S";
		}
		return vTemRespostaAna;
	}

	private String cursorFiguraAnamnese(Integer conNumero, String temFiguraAna) {
		
		Long curFan = mamFiguraAnamnesesDAO.obterCountFiguraAnamnesePorConNumero(conNumero);
		
		if (curFan > 0) {
			temFiguraAna = "S";
		} else {
			temFiguraAna = "N";
		}
		return temFiguraAna;
	}

	private String cursorNotaAdicionalAnamnese(Integer conNumero, String temNotaAna) {
		
		Long curNaa = mamNotaAdicionalAnamnesesDAO.obterCountNotaAdicionalAnamnesePorConNumero(conNumero);
		
		if (curNaa > 0) {
			temNotaAna = "S";
		} else {
			temNotaAna = "N";
		}
		return temNotaAna;
	}

	/**
	 * #50745 - P2
	 * 
	 * @ORADB P_GERA_ANAMNESE
	 */
	public GeraAnamneseVO geraAnamnese(Integer pConNumero, Long pAnaSeq) throws ApplicationBusinessException{

		Long vAnaSeqNew = null;      
		Long vAnaAnaSeqNew = null;
		Integer vPacCodigo = null;

		GeraAnamneseVO geraAnamneseVO = new GeraAnamneseVO();
		geraAnamneseVO.setSysdate(new Date());
		geraAnamneseVO.setServidor(this.servidorLogadoFacade.obterServidorLogado());

		MamAnamneses curAna = mamAnamnesesDAO.obterAnamnesePorConNumeroESeq(pConNumero, pAnaSeq);

		if (curAna == null) {
			geraAnamneseVO.setReplicaAnamnese(DominioSimNao.S);
			vPacCodigo = marcacaoConsultaRN.obterCodigoPacienteOrigem(1, pConNumero);
			
			AipPacientes paciente = pacienteFacade.obterPaciente(vPacCodigo);
			AacConsultas consulta = aacConsultasDAO.obterConsulta(pConNumero);

			MamAnamneses novaAnamnese = new MamAnamneses();
			novaAnamnese.setDthrCriacao(geraAnamneseVO.getSysdate());
			novaAnamnese.setPaciente(paciente);
			novaAnamnese.setConsulta(consulta);
			novaAnamnese.setPendente(DominioIndPendenteAmbulatorio.R);
			novaAnamnese.setServidor(geraAnamneseVO.getServidor());
			novaAnamnese.setImpresso(Boolean.FALSE);
			novaAnamnese.setIndImpressoPasta(Boolean.FALSE);
			novaAnamnese.setIndAssinaAutSist(Boolean.FALSE);
			novaAnamnese = marcacaoConsultaRN.inserirAnamnese(novaAnamnese);
		
			vAnaSeqNew = novaAnamnese.getSeq();
			vAnaAnaSeqNew = null;
		} else {
		
			geraAnamneseVO = verificaReplicaAnamnese(geraAnamneseVO, curAna);
			geraAnamneseVO = verificaExecutaPreGera(geraAnamneseVO, curAna);
			
			if (DominioIndPendenteAmbulatorio.V.equals(curAna.getPendente()) && DominioSimNao.N.equals(geraAnamneseVO.getReplicaAnamnese())) {
				vAnaSeqNew = curAna.getSeq();
				vAnaAnaSeqNew = null;
			} else if (DominioIndPendenteAmbulatorio.R.equals(curAna.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(curAna.getPendente())) {
		        vAnaSeqNew = curAna.getSeq();
				if (curAna.getAnamnese() != null) {
		        	vAnaAnaSeqNew = curAna.getAnamnese().getSeq();
		        }
			} else {
				// update anamnese
				MamAnamneses atualizaAnamnese = mamAnamnesesDAO.obterPorChavePrimaria(curAna.getSeq());
				atualizaAnamnese.setDthrMvto(geraAnamneseVO.getSysdate());
				atualizaAnamnese.setServidorMvto(geraAnamneseVO.getServidor());
				marcacaoConsultaRN.atualizarAnamnese(atualizaAnamnese);
				
				// copia a anamnese
				MamAnamneses copiaAnamnese = new MamAnamneses();
				copiaAnamnese.setDthrCriacao(geraAnamneseVO.getSysdate());
				copiaAnamnese.setPaciente(atualizaAnamnese.getPaciente());
				copiaAnamnese.setConsulta(atualizaAnamnese.getConsulta());
				copiaAnamnese.setPendente(DominioIndPendenteAmbulatorio.R);
				copiaAnamnese.setServidor(geraAnamneseVO.getServidor());
				copiaAnamnese.setAnamnese(atualizaAnamnese);
				copiaAnamnese.setImpresso(Boolean.FALSE);
				copiaAnamnese.setIndImpressoPasta(Boolean.FALSE);
				copiaAnamnese.setIndAssinaAutSist(Boolean.FALSE);
				marcacaoConsultaRN.inserirAnamnese(copiaAnamnese);
				mamAnamnesesDAO.flush();
				
		        vAnaSeqNew = copiaAnamnese.getSeq();
		        vAnaAnaSeqNew = curAna.getSeq();				
				
				// copia as respostas dadas na anamnese
				List<MamRespostaAnamneses> listaRespostaAnamneses = mamRespostaAnamnesesDAO.obterListaRespostaAnamnesePorAnaSeq(curAna.getSeq());
				for (MamRespostaAnamneses rea : listaRespostaAnamneses) {
					persistirRespostaAnamnese(rea, vAnaSeqNew);
				}
				
				// copia as respostas estruturadas das questões de anamnese
				List<MamRespQuestAnamneses> listaRespQuestAnamneses = mamRespQuestAnamnesesDAO.obterListaRespQuestAnamnesePorAnaSeq(curAna.getSeq());
				for (MamRespQuestAnamneses rqa : listaRespQuestAnamneses) {
					persistirRespQuestAnamnese(rqa, vAnaSeqNew);
				}

				// copia os itens de anamnese
				List<MamItemAnamneses> listaItemAnamneses = mamItemAnamnesesDAO.obterListaItemAnamnesePorAnaSeq(curAna.getSeq());
				for (MamItemAnamneses ian : listaItemAnamneses) {
					persistirItemAnamnese(ian, vAnaSeqNew);
				}
			}
		}
		
		geraAnamneseVO.setAnaSeq(vAnaSeqNew);
		geraAnamneseVO.setAnaAnaSeq(vAnaAnaSeqNew);

		return geraAnamneseVO;
	}
	
	private GeraAnamneseVO verificaReplicaAnamnese(GeraAnamneseVO geraAnamneseVO, MamAnamneses curAna) {
		
		if (DominioIndPendenteAmbulatorio.V.equals(curAna.getPendente())) {
			if (curAna.getServidorValida().getId().getMatricula().equals(geraAnamneseVO.getServidor().getId().getMatricula())
					&& curAna.getServidorValida().getId().getVinCodigo().equals(geraAnamneseVO.getServidor().getId().getVinCodigo())) {
				geraAnamneseVO.setReplicaAnamnese(DominioSimNao.S);
			} else {
				geraAnamneseVO.setReplicaAnamnese(DominioSimNao.N);
			}
		} else {
			geraAnamneseVO.setReplicaAnamnese(DominioSimNao.N);
		}
		return geraAnamneseVO;
	}
	
	private GeraAnamneseVO verificaExecutaPreGera(GeraAnamneseVO geraAnamneseVO, MamAnamneses curAna) {
		
		if (DominioIndPendenteAmbulatorio.V.equals(curAna.getPendente())) {
			if (curAna.getServidorValida().getId().getMatricula().equals(geraAnamneseVO.getServidor().getId().getMatricula()) && 
					curAna.getServidorValida().getId().getVinCodigo().equals(geraAnamneseVO.getServidor().getId().getVinCodigo())) {
				geraAnamneseVO.setExecutaPreGera(DominioSimNao.S);
			} else {
				geraAnamneseVO.setExecutaPreGera(DominioSimNao.N);
			}
		} else if (DominioIndPendenteAmbulatorio.P.equals(curAna.getPendente()) && curAna.getAnamnese() != null) {
			MamAnamneses curAnaOut = mamAnamnesesDAO.obterPorChavePrimaria(curAna.getAnamnese().getSeq());
			if (curAnaOut != null && curAnaOut.getServidorValida() != null
					&& curAnaOut.getServidorValida().getId().getMatricula().equals(geraAnamneseVO.getServidor().getId().getMatricula())
					&& curAnaOut.getServidorValida().getId().getVinCodigo().equals(geraAnamneseVO.getServidor().getId().getVinCodigo())) {
				geraAnamneseVO.setExecutaPreGera(DominioSimNao.S);
			} else {
				geraAnamneseVO.setExecutaPreGera(DominioSimNao.N);
			}
		} else if (curAna.getServidorValida() == null) {
			geraAnamneseVO.setExecutaPreGera(DominioSimNao.S);
		} else {
			geraAnamneseVO.setExecutaPreGera(DominioSimNao.N);
		}
		return geraAnamneseVO;
	}
	
	private void persistirRespostaAnamnese(MamRespostaAnamneses rea, Long anaSeq) throws ApplicationBusinessException {
		
		MamRespostaAnamnesesId mamRespostaAnamnesesId = new MamRespostaAnamnesesId();
		mamRespostaAnamnesesId.setAnaSeq(anaSeq);
		mamRespostaAnamnesesId.setQusQutSeq(rea.getId().getQusQutSeq());
		mamRespostaAnamnesesId.setQusSeqp(rea.getId().getQusSeqp());
		mamRespostaAnamnesesId.setSeqp(rea.getId().getSeqp());
		
		MamRespostaAnamneses mamRespostaAnamneses = new MamRespostaAnamneses();
		mamRespostaAnamneses.setId(mamRespostaAnamnesesId);
		mamRespostaAnamneses.setVvqQusQutSeq(rea.getVvqQusQutSeq());
		mamRespostaAnamneses.setVvqQusSeqp(rea.getVvqSeqp());
		mamRespostaAnamneses.setVvqSeqp(rea.getVvqSeqp());
		mamRespostaAnamneses.setResposta(rea.getResposta());
		mamRespostaAnamneses.setEspSeq(rea.getEspSeq());
		
		mamRespostaAnamnesesRN.persistirRespostaAnamnese(mamRespostaAnamneses);
	}
	
	private void persistirRespQuestAnamnese(MamRespQuestAnamneses rqa, Long anaSeq) {
		
		MamRespQuestAnamnesesId mamRespQuestAnamnesesId = new MamRespQuestAnamnesesId();
		mamRespQuestAnamnesesId.setReaAnaSeq(anaSeq);
		mamRespQuestAnamnesesId.setReaQusQutSeq(rqa.getId().getReaQusQutSeq());
		mamRespQuestAnamnesesId.setReaQusSeqp(rqa.getId().getReaQusSeqp());
		mamRespQuestAnamnesesId.setReaSeqp(rqa.getId().getReaSeqp());
		mamRespQuestAnamnesesId.setSeqp(rqa.getId().getSeqp());
		
		MamRespQuestAnamneses mamRespQuestAnamneses = new MamRespQuestAnamneses();
		mamRespQuestAnamneses.setId(mamRespQuestAnamnesesId);
		mamRespQuestAnamneses.setRvqQusQutSeq(rqa.getRvqQusQutSeq());
		mamRespQuestAnamneses.setRvqQusSeqp(rqa.getRvqQusSeqp());
		mamRespQuestAnamneses.setRvqSeqp(rqa.getRvqSeqp());
		
		mamRespQuestAnamnesesDAO.persistir(mamRespQuestAnamneses);
	}
	
	private void persistirItemAnamnese(MamItemAnamneses ian, Long anaSeq) throws ApplicationBusinessException {
		
		MamItemAnamnesesId mamItemAnamnesesId = new MamItemAnamnesesId();
		mamItemAnamnesesId.setAnaSeq(anaSeq);
		mamItemAnamnesesId.setTinSeq(ian.getId().getTinSeq());
		
		MamItemAnamneses mamItemAnamneses = new MamItemAnamneses();
		mamItemAnamneses.setId(mamItemAnamnesesId);
		mamItemAnamneses.setDescricao(ian.getDescricao());
		
		marcacaoConsultaRN.inserirItemAnamnese(mamItemAnamneses);
	}
	
	/**
	 * #50745 - Método invocado pelo botão excluir da tela de Anamnese
	 */
	public void excluirAnamnese(Integer conNumero, Long anaSeq) throws ApplicationBusinessException{

		GeraAnamneseVO geraAnamneseVO = geraAnamnese(conNumero, anaSeq);
		anaSeq = geraAnamneseVO.getAnaSeq();
		
		if (podeExcluirAnamnese(anaSeq)) {
			excluiAnamnese(conNumero, anaSeq);
		} else {
			throw new ApplicationBusinessException(AnamneseRNExceptionCode.MAM_00948);
		}
	}

	/**
	 * #50745 - P6
	 * 
	 * @ORADB PODE_EXCLUIR_ANAMNESE
	 */
	public boolean podeExcluirAnamnese(Long anaSeq) {
		Boolean podeExcluir = Boolean.FALSE;
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		AnamneseAutorelacaoVO anamnese = mamAnamnesesDAO.obterAnamneseComAutorelacaoPorSeq(anaSeq);

		if (anamnese != null) {
			if (DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente())) {
				if (anamnese.getSerMatriculaValida().equals(servidorLogado.getId().getMatricula())
						&& anamnese.getSerVinCodigoValida().equals(servidorLogado.getId().getVinCodigo())) {
					podeExcluir = Boolean.TRUE;
				} else {
					podeExcluir = Boolean.FALSE;
				}
			} else if ((DominioIndPendenteAmbulatorio.P.equals(anamnese.getPendente()) || DominioIndPendenteAmbulatorio.R.equals(anamnese.getPendente()))
					&& anamnese.getSerMatriculaValidaRelacao() != null) {
				if (anamnese.getSerMatriculaValidaRelacao().equals(servidorLogado.getId().getMatricula())
						&& anamnese.getSerVinCodigoValidaRelacao().equals(servidorLogado.getId().getVinCodigo())) {
					podeExcluir = Boolean.TRUE;
				} else {
					podeExcluir = Boolean.FALSE;
				}
			} else if (anamnese.getSerMatriculaValida() == null && anamnese.getSerVinCodigoValida() == null) {
				// é um registro novo
				podeExcluir = Boolean.TRUE;
			}
		}
		
		return podeExcluir;
	}
	
	/**
	 * #50745 - P3
	 * 
	 * @ORADB MAMP_EXCLUI_ANAMNESE
	 */
	public void excluiAnamnese(Integer conNumero, Long anaSeq) throws ApplicationBusinessException {
		RapServidores usuarioLogado = servidorLogadoFacade.obterServidorLogado();

		List<MamAnamneses> curAna = mamAnamnesesDAO.obterListaAnamnesePorConNumeroESeq(conNumero, anaSeq);
		for (MamAnamneses mamAnamneses : curAna) {
			// EXCLUI AS TEMPORÁRIAS DA ANAMNESE USADAS NOS GRÁFICOS
			cancelarDelTemp(mamAnamneses.getSeq());
			if (DominioIndPendenteAmbulatorio.R.equals(mamAnamneses.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(mamAnamneses.getPendente())) {
				pDeleteFisico(mamAnamneses.getSeq());
			}
			if (mamAnamneses.getAnamnese() != null) {
				MamAnamneses entity = mamAnamnesesDAO.obterPorChavePrimaria(mamAnamneses.getAnamnese().getSeq());
				entity.setPendente(DominioIndPendenteAmbulatorio.E);
				entity.setDthrMvto(new Date());
				entity.setServidorMvto(usuarioLogado);
				marcacaoConsultaRN.atualizarAnamnese(entity);
			}
		}

		List<MamFiguraAnamnese> cFan = mamFiguraAnamnesesDAO.obterListaFiguraAnamnesePorConNumero(conNumero);
		for (MamFiguraAnamnese mamFiguraAnamnese : cFan) {
			if ("R".equals(mamFiguraAnamnese.getIndPendente()) || "P".equals(mamFiguraAnamnese.getIndPendente())) {
				pDelFisicoFigura(mamFiguraAnamnese.getSeq());
			}
			if (mamFiguraAnamnese.getMamFiguraAnamnese() != null) {
				MamFiguraAnamnese figura = mamFiguraAnamnesesDAO.obterPorChavePrimaria(mamFiguraAnamnese.getMamFiguraAnamnese().getSeq());
				figura.setIndPendente("E");
				figura.setDthrMvto(new Date());
				figura.setRapServidoresByMamFanSerFk3(usuarioLogado);
				mamFiguraAnamnesesDAO.atualizar(figura);
			}
		}
		
		// Notas adicionais anamnese
		List<MamNotaAdicionalAnamneses> cNaa = mamNotaAdicionalAnamnesesDAO.obterListaNotaAdicionalAnamnesePorConNumero(conNumero);
		for (MamNotaAdicionalAnamneses mamNotaAdicionalAnamneses : cNaa) {
			if (DominioIndPendenteAmbulatorio.R.equals(mamNotaAdicionalAnamneses.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(mamNotaAdicionalAnamneses.getPendente())) {
				pDelFisicoNota(mamNotaAdicionalAnamneses.getSeq());
			}
			if (mamNotaAdicionalAnamneses.getNotaAdicionalAnamnese() != null) {
				MamNotaAdicionalAnamneses notaAdicional = mamNotaAdicionalAnamnesesDAO.obterPorChavePrimaria(mamNotaAdicionalAnamneses.getNotaAdicionalAnamnese().getSeq());
				notaAdicional.setPendente(DominioIndPendenteAmbulatorio.E);
				notaAdicional.setDthrMvto(new Date());
				notaAdicional.setServidorMvto(usuarioLogado);
				mamNotaAdicionalAnamnesesDAO.atualizar(notaAdicional);
			}
		}
	}

	private void pDeleteFisico(Long anaSeq) throws ApplicationBusinessException {
		List<MamItemAnamneses> mamItemAnamneses = mamItemAnamnesesDAO.pesquisarItemAnamnesesPorAnamneses(anaSeq);
		for (MamItemAnamneses item : mamItemAnamneses) {
			marcacaoConsultaRN.removerItemAnamnese(item);
		}

		List<MamRespQuestAnamneses> mamRespQuest = mamRespQuestAnamnesesDAO.obterListaRespQuestAnamnesePorAnaSeq(anaSeq);
		for (MamRespQuestAnamneses item : mamRespQuest) {
			mamRespQuestAnamnesesDAO.remover(item);
		}

		List<MamRespostaAnamneses> respostaAnamneses = mamRespostaAnamnesesDAO.obterListaRespostaAnamnesePorAnaSeq(anaSeq);
		for (MamRespostaAnamneses item : respostaAnamneses) {
			mamRespostaAnamnesesRN.removerRespostaAnamnese(item);
		}
		
		MamAnamneses mamAnamneses = mamAnamnesesDAO.obterPorChavePrimaria(anaSeq);
		marcacaoConsultaRN.removerAnamnese(mamAnamneses);
	}
	
	// Faz um delete físico da figura
	private void pDelFisicoFigura(Long fanSeq) {
		List<MamImagemAnamnese> imagemAnamnese = mamImagemAnamnesesDAO.obterListaImagemAnamnesePorFanSeq(fanSeq);
		for (MamImagemAnamnese mamImagemAnamnese : imagemAnamnese) {
			mamImagemAnamnesesDAO.remover(mamImagemAnamnese);
		}
		
		MamFiguraAnamnese figuraAnamnese = mamFiguraAnamnesesDAO.obterPorChavePrimaria(fanSeq);
		mamFiguraAnamnesesDAO.remover(figuraAnamnese);
	}

	// Faz um delete físico das notas adicionais
	private void pDelFisicoNota(Integer naaSeq) throws ApplicationBusinessException {
		MamNotaAdicionalAnamneses notaAdicional = mamNotaAdicionalAnamnesesDAO.obterPorChavePrimaria(naaSeq);
		marcacaoConsultaRN.removerNotaAdicionalAnamneses(notaAdicional);
	}

	/**
	 * #50745 - P4
	 * 
	 * @ORADB MAMP_CANC_DEL_TMP_A
	 */
	public void cancelarDelTemp(Long anaSeq) {
		Date dthrMvto = null;
		
		// -- limpa a tabela temporário dos pesos
		List<MamTmpPesos> lista = mamTmpPesosDAO.obterPesosPorAnamnese(anaSeq, dthrMvto);
		for (MamTmpPesos mamTmpPesos : lista) {
			mamTmpPesosDAO.remover(mamTmpPesos);
		}
		
		// -- limpa a tabela temporário das alturas
		List<MamTmpAlturas> listaAltura = mamTmpAlturasDAO.obterAlturaPorAnamnese(anaSeq, dthrMvto);
		for (MamTmpAlturas item : listaAltura) {
			mamTmpAlturasDAO.remover(item);
		}
		
		// -- limpa a tabela temporário dos perimetros cefalicos
		List<MamTmpPerimCefalicos> listaPerim = mamTmpPerimCefalicosDAO.obterPorAnamnese(anaSeq, dthrMvto);
		for (MamTmpPerimCefalicos item : listaPerim) {
			mamTmpPerimCefalicosDAO.remover(item);
		}
		
		// -- limpa a tabela temporário das pa sistolicas
		List<MamTmpPaSistolicas> listaPa = mamTmpPaSistolicasDAO.obterPorAnamnese(anaSeq);
		for (MamTmpPaSistolicas mamTmpPaSistolicas : listaPa) {
			mamTmpPaSistolicasDAO.remover(mamTmpPaSistolicas);
		}
		
		// -- limpa a tabela temporário das pa diastolicas
		List<MamTmpPaDiastolicas> listaDia = mamTmpPaDiastolicasDAO.obterPorAnamnese(anaSeq);
		for (MamTmpPaDiastolicas item : listaDia) {
			mamTmpPaDiastolicasDAO.remover(item);
		}
	}

	public void gravarAnamneseMotivoPendente(Integer conNumero, Long anaSeq, String motivoPendencia, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		GeraAnamneseVO anamnese = geraAnamnese(conNumero, anaSeq);
		anaSeq = anamnese.getAnaSeq();
		
		AghParametros param = null;
		if ("EXA".equals(motivoPendencia)) {
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_EXAMES);
		} else if ("PRE".equals(motivoPendencia)) {
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_PROFE);
		} else if ("POS".equals(motivoPendencia)) {
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_POST);
		} else if ("OUT".equals(motivoPendencia)) {
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_OUT);
		}
		Short satSeq = null;
		if (param.getVlrNumerico() != null) {
			satSeq = Short.valueOf(param.getVlrNumerico().toString());
		}
		cancelamentoAtendimentoRN.mampPend(conNumero, new Date(), satSeq, nomeMicrocomputador);
	}
}
