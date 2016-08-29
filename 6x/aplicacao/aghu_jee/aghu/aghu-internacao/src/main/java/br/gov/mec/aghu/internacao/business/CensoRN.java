package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentosAtendUrgenciaDAO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AinMovimentosAtendUrgencia;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 * <b><br>
 * ORADB Package AINK_CENSO.
 * </b>
 * 
 */

@Stateless 
public class CensoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CensoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@Inject
	private AinMovimentosAtendUrgenciaDAO ainMovimentosAtendUrgenciaDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
			
	/**
	 * 
	 */
	private static final long serialVersionUID = -6132290481242898556L;

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 */
	public String obtemLocalOrigem(Integer internacaoSeq, Date dataHora) {
		try {
			String origem = null;

			AinMovimentosInternacao movimento = this.getAinMovimentoInternacaoDAO().obterAinMovimentosInternacaoSeqAteDtHrLancamento(
					internacaoSeq, dataHora);

			if(movimento != null) {
				if(movimento.getLeito() != null && movimento.getLeito().getLeitoID() != null) {
					origem = movimento.getLeito().getLeitoID();
				} else if(movimento.getQuarto() != null) {
					origem = movimento.getQuarto().getDescricao();
				}
				else {
					origem = movimento.getUnidadeFuncional().getAndar() + " " + movimento.getUnidadeFuncional().getIndAla().toString(); 
				}
			}
			return origem;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}


	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 *  
	 */
	public String obtemLocalOrigemUrgencia(Integer atendimentoUrgSeq, Date dataHora) {
		try {
			String origem = null;
			
			AinMovimentosAtendUrgencia movimento = this.getAinMovimentosAtendUrgenciaDAO()
					.obterAinMovimentosAtendUrgenciaSeqAteDtHrLancamento(atendimentoUrgSeq, dataHora);
			
			if(movimento != null) {
				if(movimento.getLeito() != null && movimento.getLeito().getLeitoID() != null) {
					origem = movimento.getLeito().getLeitoID();
				} else if(movimento.getQuarto() != null) {
					origem = movimento.getQuarto().getDescricao();
				}
				else {
					origem = movimento.getUnidadeFuncional().getAndar() + " " + movimento.getUnidadeFuncional().getIndAla().toString(); 
				}
			}
			return origem;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 *  
	 */
	public String obtemLocalDestinoUrgencia(Integer atendimentoUrgenciaSeq, Date dataHora) {
		try {
			String origem = null;
			
			AinMovimentosAtendUrgencia movimento = this.getAinMovimentosAtendUrgenciaDAO()
					.obterAinMovimentosAtendUrgenciaSeqPosDtHrLancamento(atendimentoUrgenciaSeq, dataHora);

			if(movimento != null) {
				if(movimento.getLeito() != null && movimento.getLeito().getLeitoID() != null) {
					origem = movimento.getLeito().getLeitoID();
				} else if(movimento.getQuarto() != null) {
					origem = movimento.getQuarto().getDescricao();
				}
				else {
					origem = movimento.getUnidadeFuncional().getAndar() + " " + movimento.getUnidadeFuncional().getIndAla().toString(); 
				}
			}
			return origem;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 *  
	 */
	public String obtemLocalDestino(Integer internacaoSeq, Date dataHora) {
		try {
			String origem = null;
									
			AinMovimentosInternacao movimento = this.getAinMovimentoInternacaoDAO().obterAinMovimentosInternacaoSeqPosDtHrLancamento(
					internacaoSeq, dataHora);
			
			if(movimento != null) {
				if(movimento.getLeito() != null && movimento.getLeito().getLeitoID() != null) {
					origem = movimento.getLeito().getLeitoID();
				} else if(movimento.getQuarto() != null) {
					origem = movimento.getQuarto().getDescricao();
				}
				else {
					origem = movimento.getUnidadeFuncional().getAndar() + " " + movimento.getUnidadeFuncional().getIndAla().toString(); 
				}
			}
			return origem;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL
	 * .
	 *  
	 */
	public Date obtemDthrFinal(Integer internacaoSeq, Date dataHora) {
		try {
			return this.getAinMovimentoInternacaoDAO().obtemDthrFinal(internacaoSeq, dataHora);
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL
	 * .
	 *  
	 */
	public Date obtemDthrFinalUrgencia(Integer atendimentoUrgenciaSeq, Date dataHora) {
		try {
			return this.getAinMovimentosAtendUrgenciaDAO().obtemDthrFinalUrgencia(atendimentoUrgenciaSeq, dataHora);			
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_T .
	 * 
	 */
	public Date obtemDthrFinalTUrgencia(Integer atendimentoUrgenciaSeq, Date dataHora) {
		try {			
			Date dthrLancamento = this.getAinMovimentosAtendUrgenciaDAO().obtemDthrFinalUrgencia(atendimentoUrgenciaSeq, dataHora);
			if (dthrLancamento != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(dthrLancamento);
				c.set(Calendar.HOUR_OF_DAY, 23);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				return c.getTime();
			}
			return null;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_NOVA
	 * .
	 *  
	 */
	public Date obtemDthrFinalNova(Integer atendimentoUrgenciaSeq, Date dataHora, Integer movimentoSeq, Date criadoEm) {
		try {
			return this.getAinMovimentosAtendUrgenciaDAO()
					.obtemDthrFinalNova(atendimentoUrgenciaSeq, dataHora, movimentoSeq, criadoEm);
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_T
	 * .
	 *  
	 */
	public Date obtemDthrFinalT(Integer internacaoSeq, Date dataHora) {
		try {
			Date dthrLancamento = this.getAinMovimentoInternacaoDAO().obtemDthrFinal(internacaoSeq, dataHora);
			if(dthrLancamento != null){
				Calendar c = Calendar.getInstance();
				c.setTime(dthrLancamento);
				c.set(Calendar.HOUR_OF_DAY, 23);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				return c.getTime();
			}
			return null;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO.UNF_SEQ_ORIGEM
	 * 
	 * Obtem local de origem (sequence).
	 *  
	 */
	public Short obtemLocalOrigemSeq(Integer internacaoSeq, Date dataHora) {
		try {
			Short seq = this.getAinMovimentoInternacaoDAO().obtemLocalOrigemSeq(internacaoSeq, dataHora);
			return seq;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_LTO
	 * 
	 * Obtem data final leito.
	 *  
	 */
	public Date obtemDthrFinalLeito(String leitoId, Date dataHora) {
		return this.getAinExtratoLeitosDAO().obtemDthrFinalLeito(leitoId, dataHora);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_MOV
	 * 
	 * Obtem local de origem.
	 *  
	 */
	public String obtemOrigemMovimentacao(Integer intSeq) {
		try {
			String origem = null;
			
			List<Object[]> lista = this.getAinMovimentoInternacaoDAO().pesquisarOrigemPorInternacao(intSeq);

			if(lista != null && lista.size() > 1) {
				int c = 1;
				for(Object[] obj : lista){
					if(obj[0] != null) {
						origem = (String)obj[0];
					} else if(obj[1] != null) {
						origem = ((Short)obj[1]).toString();
					} else {
						Object[] objUnf = this.getAghuFacade().obterAndarAlaPorSeq((Short)obj[2]);
						if (objUnf != null) {
							origem  = (objUnf[0]).toString() + " " + ((AghAla)objUnf[1]).toString(); 
						}
					}
					if(c == 2) {
						break;
					}
					c++;
				}
			}
			else {
				origem = " ";
			}
			return origem;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_ATU
	 * 
	 * Obtem local de origem do atendimento de urgencia.
	 *  
	 */
	public String obtemOrigemAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		try {
			String origem = null;
			
			List<Object[]> lista = this.getAinMovimentosAtendUrgenciaDAO().pesquisarOrigemPorAtendimentoUrgencia(atendimentoUrgenciaSeq);

			if(lista != null && lista.size() > 1) {
				int c = 1;
				for(Object[] obj : lista){
					if(obj[0] != null) {
						origem = (String)obj[0];
					}
					else if(obj[1] != null) {
						origem = ((Short)obj[1]).toString();
					}
					else {
						Object[] objUnf = this.getAghuFacade().obterAndarAlaPorSeq((Short)obj[2]);
						if(objUnf != null) {
							origem  = ((Byte)objUnf[0]).toString() + " " + ((AghAla)objUnf[1]).toString(); 
						}
					}
					if(c == 2) {
						break;
					}
					c++;					
				}
			}
			else {
				origem = " ";
			}
			return origem;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_FIN
	 * 
	 * Obtem data final.
	 *  
	 */
	public Date buscaDthrFinal(Integer intSeq) {
		try {
			return this.getAinMovimentoInternacaoDAO().buscaDthrFinal(intSeq);
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_LTO
	 * 
	 * Obtem data final leito.
	 *  
	 */
	public Date buscaDthrLeito(String leitoId) {
		try {
			return this.getAinExtratoLeitosDAO().buscaDthrLeito(leitoId);
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_ATU
	 * 
	 * Obtem data atendimento de urgencia.
	 *  
	 */
	public Date buscaDthrAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		try {
			Date data = this.getAinMovimentosAtendUrgenciaDAO().buscaDthrAtendimentoUrgencia(atendimentoUrgenciaSeq);			
			return data;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}
	
	protected AinMovimentosAtendUrgenciaDAO getAinMovimentosAtendUrgenciaDAO() {
		return ainMovimentosAtendUrgenciaDAO;
	}
	
	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
	
}
