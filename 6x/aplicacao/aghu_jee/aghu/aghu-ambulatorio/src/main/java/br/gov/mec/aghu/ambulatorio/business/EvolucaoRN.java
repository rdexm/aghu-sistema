package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.vo.ExamesLiberadosVO;
import br.gov.mec.aghu.ambulatorio.vo.ResultadoExameVO;
import br.gov.mec.aghu.casca.dao.CseCategoriaProfissionalDAO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoesId;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EvolucaoRN extends BaseBusiness {
	
	private static final long serialVersionUID = 8820889177782705396L;
	
	private static final Log LOG = LogFactory.getLog(EvolucaoRN.class);
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
		
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@Inject
	private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;
	
	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;
	
	@Inject
	private CseCategoriaProfissionalDAO cseCategoriaProfissionalDAO;
	
	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;	
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	IServidorLogadoFacade servidorLogadoFacade; 
	
	/**
	 * ORADB: copia_escolhidos
	 * @param evoSeq
	 * @param examesSelecionados
	 */
	public void copiarEscolhidos(Long evoSeq, List<ExamesLiberadosVO> examesSelecionados) {
		
		Integer cagSeq = null;
		
		List<CseCategoriaProfissional> categoriaProfissional = cseCategoriaProfissionalDAO.pesquisarCategoriaProfissional(servidorLogadoFacade.obterServidorLogado());
		if (categoriaProfissional != null && !categoriaProfissional.isEmpty()) {
			cagSeq = categoriaProfissional.get(0).getSeq();
		}
		
		String exames = "";
		String amostra = "";
		String resultado = "";
		for (ExamesLiberadosVO selecionado: examesSelecionados) {
			amostra="";
			resultado = "";
			if (selecionado.getAmostra() != null) {
				amostra = selecionado.getAmostra();
			}
			if (selecionado.getResultado() != null) {
				resultado = selecionado.getResultado();
			}
			exames = exames
					.concat(selecionado.getNomeExame()).concat(" - ")
					.concat(amostra).concat(": ")
					.concat(resultado).concat(" (")
					.concat(DateUtil.obterDataFormatada(
							selecionado.getDataExame(), "dd/MM/yyyy")).concat(") ")
					.concat("\n");
		}
		
		List<MamTipoItemEvolucao> listaTipoItemEvolucao = mamTipoItemEvolucaoDAO.obterTipoItensAtivosPorCategoria(cagSeq);
		for (MamTipoItemEvolucao tie : listaTipoItemEvolucao) {
			if (mamItemEvolucoesDAO.pesquisarExisteItemEvolucaoPorEvolucaoTipoItem(evoSeq, tie.getSeq())) {
				MamItemEvolucoes mamItemEvolucoes = new MamItemEvolucoes();
				MamItemEvolucoesId mamItemEvolucoesId = new MamItemEvolucoesId();
				mamItemEvolucoesId.setEvoSeq(evoSeq);
				mamItemEvolucoesId.setTieSeq(tie.getSeq());
				mamItemEvolucoes.setId(mamItemEvolucoesId);
				mamItemEvolucoes = mamItemEvolucoesDAO.obterPorChavePrimaria(mamItemEvolucoesId);
				mamItemEvolucoes.setDescricao(mamItemEvolucoes.getDescricao().concat("\n").concat(exames));
				mamItemEvolucoesDAO.atualizar(mamItemEvolucoes);
			} else {
				MamItemEvolucoes mamItemEvolucoes = new MamItemEvolucoes();
				MamItemEvolucoesId mamItemEvolucoesId = new MamItemEvolucoesId();
				mamItemEvolucoesId.setEvoSeq(evoSeq);
				mamItemEvolucoesId.setTieSeq(tie.getSeq());
				mamItemEvolucoes.setId(mamItemEvolucoesId);
				mamItemEvolucoes.setDescricao(exames);
				mamItemEvolucoesDAO.persistir(mamItemEvolucoes);
			}
		}
	}

	/**
	 * ORADB: monta_tela
	 * @param pacCodigo
	 * @param numeroConsulta
	 * @return List<ExamesLiberadosVO>
	 * @throws ApplicationBusinessException
	 */
	public List<ExamesLiberadosVO> montarTelaExamesLiberados(Integer pacCodigo, Integer numeroConsulta) throws ApplicationBusinessException {
		
		String sitExecutando = "LI";
		
		String sitLiberado = null;
		AghParametros parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		if (parametro != null) {
			sitLiberado = parametro.getVlrTexto();
		}
		
		String sitAreaExecutora = null;
		parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		if (parametro != null) {
			sitAreaExecutora = parametro.getVlrTexto();
		}
		
		BigDecimal tempoMinutosSolic = null;
		parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_TEMPO_MINUTOS_FLUXO);
		if (parametro != null) {
			tempoMinutosSolic = parametro.getVlrNumerico();
		}
		
		if (pacCodigo == null) {
			pacCodigo = marcacaoConsultaRN.obterCodigoPacienteOrigem(1, numeroConsulta);
		}
		
		Set<ExamesLiberadosVO> examesLiberados = new HashSet<ExamesLiberadosVO>();
		List<ExamesLiberadosVO> examesLiberados01 = aelItemSolicitacaoExameDAO.montarExamesLiberados01(pacCodigo);
		List<ExamesLiberadosVO> examesLiberados02 = aelItemSolicitacaoExameDAO.montarExamesLiberados02(pacCodigo);
		List<ExamesLiberadosVO> examesLiberados03 = aelItemSolicitacaoExameDAO.montarExamesLiberados03(pacCodigo, sitLiberado,sitExecutando, sitAreaExecutora, tempoMinutosSolic);
		montarListaExamesLiberadosParte1(sitLiberado, sitAreaExecutora,
				examesLiberados, examesLiberados01);
		montarListaExamesLiberadosParte1(sitLiberado, sitAreaExecutora,
				examesLiberados, examesLiberados02);
		montarListaExamesLiberadosParte2(examesLiberados, examesLiberados03);
		
		List<ExamesLiberadosVO> listaRetorno = new ArrayList<ExamesLiberadosVO>(examesLiberados);
		if (!listaRetorno.isEmpty()) {
			ordenarLista(listaRetorno);
			Collections.reverse(listaRetorno);
		}
		
		return listaRetorno;
	}

	private void montarListaExamesLiberadosParte2(
			Set<ExamesLiberadosVO> examesLiberados,
			List<ExamesLiberadosVO> examesLiberados03) {
		if (!examesLiberados03.isEmpty()) {
			BigDecimal resultado = null;
			for (ExamesLiberadosVO vo : examesLiberados03) {
				resultado = null;
				vo.setOrigem("F");
				vo.setResultado(null);
				vo.setPrimResultado(this.obterPrimeiroResultadoExame(vo.getSoeSeq(), vo.getSeqp()));
				
				if (vo.getPrimResultado() == null || vo.getPrimResultado().length() > 10) {
					if (vo.getQtdeCasasDecimais() == null) {
						resultado = BigDecimal.valueOf(vo.getValor());
					} else {
						resultado = BigDecimal.valueOf(vo.getValor() / Math.pow(10, vo.getQtdeCasasDecimais()));
					}
					if (vo.getIndDividePorMil()) {
						resultado = resultado.divide(BigDecimal.valueOf(1000L));
					}
				}
				if (resultado != null) {
					vo.setResultado(resultado.toString());
				}
					
			}
			examesLiberados.addAll(examesLiberados03);
		}
	}

	private void montarListaExamesLiberadosParte1(String sitLiberado,
			String sitAreaExecutora, Set<ExamesLiberadosVO> examesLiberados,
			List<ExamesLiberadosVO> examesLiberados01) {
		if (!examesLiberados01.isEmpty()) {
			String resultado = null;
			for (ExamesLiberadosVO vo : examesLiberados01) {
				resultado = null;
				vo.setOrigem("E");
				vo.setDataExame(examesFacade.obterDataExame(vo.getSoeSeq(), vo.getSeqp(), sitLiberado, sitAreaExecutora));
				vo.setResultado(this.obterPrimeiroResultadoExame(vo.getSoeSeq(), vo.getSeqp()));
				vo.setPrimResultado("0");
				vo.setIndDividePorMil(Boolean.FALSE);
				vo.setQtdeCasasDecimais(Short.valueOf("0"));
				vo.setValor(0L);
				
				if (vo.getResultado() != null && vo.getResultado().length() <= 10) {
					resultado = vo.getResultado();
				}
				vo.setResultado(resultado);
			}
			examesLiberados.addAll(examesLiberados01);
		}
	}
	
	/**
	 * ORADB: AELC_GET_PRIM_RESU
	 * @param soeSeq
	 * @param seqp
	 * @return String
	 */
	public String obterPrimeiroResultadoExame(Integer soeSeq, Short seqp) {
		
		Long total = aelResultadoExameDAO.contarResultadoExame(soeSeq, seqp);
		if (total > 1) {
			return null;
		}
		
		ResultadoExameVO cResultado = aelResultadoExameDAO.obterResultadoExamePorItemSolicitacaoExame(soeSeq, seqp);
		
		if (cResultado == null) {			
			return null;
		} else {
			if (cResultado.getValor() != null) {
				return cResultado.getValor().toString();
			}
			
			if (cResultado.getRcdGtcSeq() != null && cResultado.getRcdSeqp() != null) {
				String cCodif = aelResultadoCodificadoDAO.obterDescricao(cResultado.getRcdGtcSeq(), cResultado.getRcdSeqp());
				if (cCodif == null) {
					return null;
				}
				if (cCodif.length() <= 20) {
					return cCodif;
				} else {
					return null;
				}
			}
			
			if (cResultado.getCacSeq() != null) {
				String cCarac = aelResultadoCaracteristicaDAO.obterDescricao(cResultado.getCacSeq());
				if (cCarac == null) {
					return null;
				}
				if (cCarac.length() <= 20) {
					return cCarac;
				} else {
					return null;
				}
			}
			
			String cDescrResultado = cResultado.getDescricao();
			
			if (cDescrResultado == null) {
				return null;
			}
			if (cDescrResultado.length() <= 20) {
				return cDescrResultado;
			} else {
				return null;
			}
									
		}
	}
	
	/**
	 * Ordena lista de exames liberados.
	 * @param lista
	 */
	private void ordenarLista(List<ExamesLiberadosVO> lista) {
		Collections.sort(lista, new Comparator<ExamesLiberadosVO>() {
			public int compare(ExamesLiberadosVO o1, ExamesLiberadosVO o2) {
				if (o1.getDataExame() == null) {
					return -1;
				}
				if (o2.getDataExame() == null) {
					return 1;
				}
		    	if (o1.getDataExame() != null && o2.getDataExame() != null) {
		          Date data1 = o1.getDataExame();
		          Date data2 = o2.getDataExame();

		          return data1.compareTo(data2);
		       } 
		       return 0;    
		    }
		});		
	}
	
	@Override
	protected Log getLogger() {		
		return LOG;
	}

}
