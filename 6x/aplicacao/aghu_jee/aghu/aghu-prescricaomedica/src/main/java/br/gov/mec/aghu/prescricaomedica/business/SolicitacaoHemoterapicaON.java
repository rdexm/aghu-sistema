package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.internal.util.SerializationHelper;

import br.gov.mec.aghu.bancosangue.vo.ItemSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.JustificativaComponenteSanguineoVO;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativaId;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CompSanguineoProcedHemoterapicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelAtendimentoCidSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelItemComponenteSangSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelItemProcedimentoHemoSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicasJustificativaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.NPathComplexity"})
@Stateless
public class SolicitacaoHemoterapicaON extends BaseBusiness {

	@EJB
	private PrescricaoMedicaON prescricaoMedicaON;
	
	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoHemoterapicaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7075259297606917499L;

	private enum SolicitacaoHemoterapicaONExceptionCode implements
			BusinessExceptionCode {
		ERRO_SOLICITACAO_HEMOTERAPICA_SEM_JUSTIFICATIVA

	}
	
	private static final Comparator<CompSanguineoProcedHemoterapicoVO> COMP_SANGUINEO_PROCED_HEMOTERAPICO_COMPARATOR = new Comparator<CompSanguineoProcedHemoterapicoVO>() {

		@Override
		public int compare(CompSanguineoProcedHemoterapicoVO o1,
				CompSanguineoProcedHemoterapicoVO o2) {
			return o1.getDescricao().compareToIgnoreCase(o2.getDescricao());

		}
	};
	
	public void removerSolicitacaoHemoterapica (AbsSolicitacoesHemoterapicas solicHemo, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas = getBancoDeSangueFacade().obterSolicitacoesHemoterapicas(
				solicHemo.getId().getAtdSeq()
				, solicHemo.getId().getSeq()
		);
		
		if (DominioIndPendenteItemPrescricao.N.equals(solicitacoesHemoterapicas.getIndPendente())) {
			solicitacoesHemoterapicas.setIndPendente(DominioIndPendenteItemPrescricao.E);
			
			MpmPrescricaoMedica prescricaoMedicaLoaded = solicitacoesHemoterapicas.getPrescricaoMedica();
			solicitacoesHemoterapicas.setDthrFim(
					getPrescricaoMedicaON().isPrescricaoVigente(prescricaoMedicaLoaded)
						? prescricaoMedicaLoaded.getDthrMovimento()
						: prescricaoMedicaLoaded.getDthrInicio()
			);
			solicitacoesHemoterapicas.setServidorMovimentado(servidorLogado);
			solicitacoesHemoterapicas.setAlteradoEm(new Date());
			this.persistirSolicitacaoHemoterapica(solicitacoesHemoterapicas, nomeMicrocomputador);
		} else if (DominioIndPendenteItemPrescricao.P
				.equals(solicitacoesHemoterapicas.getIndPendente())) {
			if (solicitacoesHemoterapicas.getSolicitacaoHemoterapica() == null) {
				this.excluirSolicitacaoHemoterapica(solicitacoesHemoterapicas);
			} else {
				AbsSolicitacoesHemoterapicas autoRelacionamento = this.getBancoDeSangueFacade().obterSolicitacoesHemoterapicasComItensSolicitacoes(
						solicitacoesHemoterapicas.getSolicitacaoHemoterapica().getId().getAtdSeq()
						, solicitacoesHemoterapicas.getSolicitacaoHemoterapica().getId().getSeq()
				);
				this.excluirSolicitacaoHemoterapica(solicitacoesHemoterapicas);
				
				autoRelacionamento.setIndPendente(DominioIndPendenteItemPrescricao.E);
				autoRelacionamento.setServidorMovimentado(servidorLogado);
				this.persistirSolicitacaoHemoterapica(autoRelacionamento, nomeMicrocomputador);
			}
		}
	}

	
	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}
	
	public void excluirJustificativaItemSolicitacaoHemoterapica(
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException {
		getBancoDeSangueFacade()
				.excluirJustificativaItemSolicitacaoHemoterapica(
						itemSolicitacaoHemoterapicaJustificativa);
	}

	public void excluirSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica)
	throws ApplicationBusinessException {
//		if (solicitacaoHemoterapica.getItensSolHemoterapicas() != null) {
//			for (AbsItensSolHemoterapicas itenSolHemoterapica : solicitacaoHemoterapica
//					.getItensSolHemoterapicas()) {
//				getBancoDeSangueFacade()
//						.excluirItemSolicitacaoHemoterapica(
//								itenSolHemoterapica);
//			}
//		}
		
		getBancoDeSangueFacade().excluirSolicitacaoHemoterapica(
				solicitacaoHemoterapica);

//		super.flush();
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public AbsSolicitacoesHemoterapicas persistirSolicitacaoHemoterapica(AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
		throws BaseRuntimeException, BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Inserção
		//solicitacaoHemoterapica = reatachar(solicitacaoHemoterapica);
		if (solicitacaoHemoterapica.getSolicitacaoHemoterapica() != null) {
			AbsSolicitacoesHemoterapicas solicHemo = this.getBancoDeSangueFacade().obterSolicitacoesHemoterapicas(
					solicitacaoHemoterapica.getSolicitacaoHemoterapica().getId().getAtdSeq()
					, solicitacaoHemoterapica.getSolicitacaoHemoterapica().getId().getSeq()
			);
			if (DominioIndPendenteItemPrescricao.N.equals(solicHemo.getIndPendente())
					&& DominioIndPendenteItemPrescricao.P.equals(solicitacaoHemoterapica.getIndPendente())) {
				solicHemo.setIndPendente(DominioIndPendenteItemPrescricao.A);
				solicHemo.setDthrFim(
							getPrescricaoMedicaON().isPrescricaoVigente(solicitacaoHemoterapica.getPrescricaoMedica()) 
									? solicitacaoHemoterapica.getPrescricaoMedica().getDthrMovimento() 
									: solicitacaoHemoterapica.getPrescricaoMedica().getDthrInicio());
				solicHemo.setServidorMovimentado(servidorLogado);
				this.persistirSolicitacaoHemoterapica(solicHemo, nomeMicrocomputador);
			}
		}
		
		if (solicitacaoHemoterapica.getId() == null || solicitacaoHemoterapica.getId().getSeq() == null) {
			getBancoDeSangueFacade().inserirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
			
			if (solicitacaoHemoterapica.getItensSolHemoterapicas() != null) {
				short sequencia = 1;
				List<AbsItensSolHemoterapicas> itens = new ArrayList<AbsItensSolHemoterapicas>(0);
				for (AbsItensSolHemoterapicas itemSolHemoterapica : solicitacaoHemoterapica.getItensSolHemoterapicas()) {
					// itemSolHemoterapica =
					// itemSolicitacaoHemoterapicaRN.reatachar(itemSolHemoterapica);

					AbsItensSolHemoterapicasId idItem = new AbsItensSolHemoterapicasId();
					idItem.setSheAtdSeq(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq());
					idItem.setSheSeq(solicitacaoHemoterapica.getId().getSeq());
					idItem.setSequencia(sequencia);
					
					itemSolHemoterapica.setId(idItem);

					itemSolHemoterapica = getBancoDeSangueFacade().inserirItemSolicitacaoHemoterapica(itemSolHemoterapica);
					
					//SALAVAR JUSTIFICATIVA
					if (itemSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas() != null) {
						List<AbsItemSolicitacaoHemoterapicaJustificativa> justificativas = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(0);
						for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa 
								: itemSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas()) {
							// getJustificativaComponenteSanguineoRN().gravarJustificativaComponenteSanguineo(
							// itemSolicitacaoHemoterapicaJustificativa.getJustificativaComponenteSanguineo())
							AbsItemSolicitacaoHemoterapicaJustificativaId idItemHemJus = new AbsItemSolicitacaoHemoterapicaJustificativaId();
							idItemHemJus.setIshSheAtdSeq(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq());
							idItemHemJus.setIshSheSeq(solicitacaoHemoterapica.getId().getSeq());
							idItemHemJus.setIshSequencia(itemSolHemoterapica.getId().getSequencia());
							idItemHemJus.setJcsSeq(itemSolicitacaoHemoterapicaJustificativa.getJustificativaComponenteSanguineo().getSeq());
							itemSolicitacaoHemoterapicaJustificativa.setId(idItemHemJus);
							itemSolicitacaoHemoterapicaJustificativa = 
									getBancoDeSangueFacade().inserirJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
							justificativas.add(itemSolicitacaoHemoterapicaJustificativa);
						}
						itemSolHemoterapica.setItemSolicitacaoHemoterapicaJustificativas(justificativas);
					}
											
					sequencia++;
					itens.add(itemSolHemoterapica);
				}
				solicitacaoHemoterapica.setItensSolHemoterapicas(itens);
			}
			super.flush();
			
		} else {
			// Edição
			short sequencia = 1;
			// OBTER VO ITENS - REMOVER OS QUE NÃO EXISTEM MAIS NA
			// COLLECTION
			List<ItemSolicitacaoHemoterapicaVO> listaItensVO = getBancoDeSangueFacade().obterListaItemSolicitacaoHemoterapicaVO(
							solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq()
							, solicitacaoHemoterapica.getId().getSeq());
			boolean remove = true;
			for (Iterator<ItemSolicitacaoHemoterapicaVO> itr = listaItensVO.iterator(); itr.hasNext();) {
				ItemSolicitacaoHemoterapicaVO vo = itr.next();
				remove = false;
				
				if(sequencia < vo.getSequencia()) {
					sequencia = vo.getSequencia().shortValue();
				}
				
				for (AbsItensSolHemoterapicas itensSolHemoterapicas : solicitacaoHemoterapica.getItensSolHemoterapicas()) {
					if (itensSolHemoterapicas.getId() != null
							&& vo.getSequencia() != null
							&& vo.getSequencia().equals(itensSolHemoterapicas.getId().getSequencia())) {
							remove = true;
							break;
						}
				}
				if (remove) {
					itr.remove();
				}
			}
			
			for (ItemSolicitacaoHemoterapicaVO vo : listaItensVO) {
				AbsItensSolHemoterapicas item = getBancoDeSangueFacade().obterItemSolicitacaoHemoterapicaVO(
								new AbsItensSolHemoterapicasId(
										vo.getSheAtdSeq()
										, vo.getSheSeq()
										, vo.getSequencia())
				);
				getBancoDeSangueFacade().excluirItemSolicitacaoHemoterapica(item);
			}
							
			sequencia++;
			
			if (solicitacaoHemoterapica.getItensSolHemoterapicas() != null) {
				List<AbsItensSolHemoterapicas> itens = new ArrayList<AbsItensSolHemoterapicas>(0);
				for (AbsItensSolHemoterapicas itensSolHemoterapica : solicitacaoHemoterapica.getItensSolHemoterapicas()) {
					if (itensSolHemoterapica.getId() == null || itensSolHemoterapica.getId().getSequencia() == null) {
						AbsItensSolHemoterapicasId idItem = new AbsItensSolHemoterapicasId();
						idItem.setSheAtdSeq(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq());
						idItem.setSheSeq(solicitacaoHemoterapica.getId().getSeq());
						idItem.setSequencia(sequencia);
						
						itensSolHemoterapica.setId(idItem);

						itensSolHemoterapica = getBancoDeSangueFacade().inserirItemSolicitacaoHemoterapica(itensSolHemoterapica);

						//SALAVAR JUSTIFICATIVA
						if (itensSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas() != null) {
							List<AbsItemSolicitacaoHemoterapicaJustificativa> justificativas = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(0);
							
							for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa : itensSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas()) {
								//getJustificativaComponenteSanguineoRN().gravarJustificativaComponenteSanguineo(itemSolicitacaoHemoterapicaJustificativa.getJustificativaComponenteSanguineo());
								AbsItemSolicitacaoHemoterapicaJustificativaId idItemHemJus = new AbsItemSolicitacaoHemoterapicaJustificativaId();
								idItemHemJus.setIshSheAtdSeq(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq());
								idItemHemJus.setIshSheSeq(solicitacaoHemoterapica.getId().getSeq());
								idItemHemJus.setIshSequencia(itensSolHemoterapica.getId().getSequencia());
								idItemHemJus.setJcsSeq(itemSolicitacaoHemoterapicaJustificativa.getJustificativaComponenteSanguineo().getSeq());
								itemSolicitacaoHemoterapicaJustificativa.setId(idItemHemJus);
								itemSolicitacaoHemoterapicaJustificativa = getBancoDeSangueFacade().inserirJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
								justificativas.add(itemSolicitacaoHemoterapicaJustificativa);
							}
							itensSolHemoterapica.setItemSolicitacaoHemoterapicaJustificativas(justificativas);
						}
						
						sequencia++;
					} else {
						//SALAVAR JUSTIFICATIVA
						if (itensSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas() != null) {
							List<AbsItemSolicitacaoHemoterapicaJustificativa> justificativas = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(0);
							for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa 
									: itensSolHemoterapica.getItemSolicitacaoHemoterapicaJustificativas()) {
								if (itemSolicitacaoHemoterapicaJustificativa.getId() == null) {
									AbsItemSolicitacaoHemoterapicaJustificativaId idItemHemJus = new AbsItemSolicitacaoHemoterapicaJustificativaId();
									idItemHemJus.setIshSheAtdSeq(
											solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq());
									idItemHemJus.setIshSheSeq(solicitacaoHemoterapica.getId().getSeq());
									idItemHemJus.setIshSequencia(itensSolHemoterapica.getId().getSequencia());
									idItemHemJus.setJcsSeq(itemSolicitacaoHemoterapicaJustificativa.getJustificativaComponenteSanguineo().getSeq());
									itemSolicitacaoHemoterapicaJustificativa.setId(idItemHemJus);
								}
								itemSolicitacaoHemoterapicaJustificativa = getBancoDeSangueFacade().atualizarJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
								justificativas.add(itemSolicitacaoHemoterapicaJustificativa);
							}
							itensSolHemoterapica.setItemSolicitacaoHemoterapicaJustificativas(justificativas);
						}	
						
						itensSolHemoterapica = getBancoDeSangueFacade().atualizarItemSolicitacaoHemoterapica(itensSolHemoterapica);

					}
					itens.add(itensSolHemoterapica);
				}
				solicitacaoHemoterapica.setItensSolHemoterapicas(itens);
			}
			

			solicitacaoHemoterapica = getBancoDeSangueFacade().atualizarSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
			super.flush();
		}
		
		return solicitacaoHemoterapica;
	}
	
	/**
	 * Método que retorna os objetos VO que serão utilizados no relatório de
	 * hemoterapias
	 * 
	 * @param prescricaoMedica
	 * @param dataMovimento 
	 * @return
	 * @throws BaseException 
	 *  
	 */
	public List<RelSolHemoterapicaVO> pesquisarRelSolHemoterapicaVOs(
			MpmPrescricaoMedica prescricaoMedica,
			EnumTipoImpressao tipoImpressao, RapServidores servidorValida, Date dataMovimento) throws BaseException {
		PrescricaoMedicaON prescricaoMedicaON = this.getPrescricaoMedicaON();
		
		List<AbsSolicitacoesHemoterapicas> listaHemoterapias = getBancoDeSangueFacade()
				.pesquisarSolicitacoesHemoterapicasRelatorio(prescricaoMedica);
		List<RelSolHemoterapicaVO> listaRelSolHemoterapicaVO = new ArrayList<RelSolHemoterapicaVO>();
		
		/*---------------VERIFICA SE É REIMPRESSÃO ---------------------*/
		Boolean impressaoTotal = this.verificarReimpressao(tipoImpressao, servidorValida);
		/*--------------------------------------------------------------*/
		
		for (AbsSolicitacoesHemoterapicas hemoterapia: listaHemoterapias){
			RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
			
			//Atribui os valores
			this.realizarAtribuicoesAoVO(prescricaoMedica, hemoterapia,
					relSolHemoterapicaVO);
			
			/*
			 * TODO: RETIRAR ESTE REFRESH QUANDO FOR RESOLVIDO O PROBLEMA DO
			 * EDITAR HEMOTERAPIA PELO BOTÃO "GRAVAR"
			 */
			getBancoDeSangueFacade().refreshSolicitacoesHeoterapicas(hemoterapia);
			
			EnumStatusItem statusItem = prescricaoMedicaON
					.buscarStatusItem(hemoterapia, dataMovimento);
			if (!impressaoTotal){
				//Registra somente as movimentações
				this.providenciarSomenteMovimentacoes(statusItem,
						relSolHemoterapicaVO, listaRelSolHemoterapicaVO);
			} else {
				//Registra todas as hemoterapias (reimpressão)
				this.providenciarReimpressao(tipoImpressao, statusItem,
						relSolHemoterapicaVO, listaRelSolHemoterapicaVO,
						hemoterapia);
			}
		}
		return listaRelSolHemoterapicaVO;
	}
	
	/**
	 * Método que providencia as atribuições ao objeto VO que será exibido no
	 * relatório
	 * 
	 * @param prescricaoMedica
	 * @param hemoterapia
	 * @param relSolHemoterapicaVO
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void realizarAtribuicoesAoVO(MpmPrescricaoMedica prescricaoMedica,
			AbsSolicitacoesHemoterapicas hemoterapia,
			RelSolHemoterapicaVO relSolHemoterapicaVO)
			throws BaseException {
		
		BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO = getLaudoProcedimentoSusRN()
		.buscaConselhoProfissionalServidorVO(null);

		relSolHemoterapicaVO.setSolicitante(buscaConselhoProfissionalServidorVO
						.getNome());
		
		if (buscaConselhoProfissionalServidorVO.getSiglaConselho() != null) {
			relSolHemoterapicaVO
			.setSiglaConselho(buscaConselhoProfissionalServidorVO
					.getSiglaConselho()+":");
		} else {
			relSolHemoterapicaVO.setSiglaConselho("");
		}
		
		relSolHemoterapicaVO
				.setNumeroRegistroConselho(buscaConselhoProfissionalServidorVO
						.getNumeroRegistroConselho());
		
		relSolHemoterapicaVO.setSeq(hemoterapia.getId().getSeq());
		
		this.atribuirValoresPrescricao(prescricaoMedica, relSolHemoterapicaVO);
		this.atribuirValoresHemoterapia(hemoterapia, relSolHemoterapicaVO);
		
		//Seta a lista de Componentes Sanguíneos
		relSolHemoterapicaVO.setListaComponentesSanguineos(this
				.pesquisarItensComponentesSanguineosRel(hemoterapia));
		
		//Seta a lista de Procedimentos
		relSolHemoterapicaVO.setListaProcedimentosHemoterapicos(this
				.pesquisarItensProcedimentosRel(hemoterapia));
		
		//Seta a lista de descrições de cids de atendimento
		relSolHemoterapicaVO.setListaCidsAtendimento(this
				.pesquisarCidsAtendimento(prescricaoMedica.getAtendimento()));
	}
	
	
	/**
	 * Método que providencia a exibição de todas as hemoterapias (reimpressão)
	 * 
	 * @param tipoImpressao
	 * @param statusItem
	 * @param relSolHemoterapicaVO
	 * @param listaRelSolHemoterapicaVO
	 * @param hemoterapia
	 */
	protected void providenciarReimpressao(EnumTipoImpressao tipoImpressao,
			EnumStatusItem statusItem,
			RelSolHemoterapicaVO relSolHemoterapicaVO,
			List<RelSolHemoterapicaVO> listaRelSolHemoterapicaVO,
			AbsSolicitacoesHemoterapicas hemoterapia) {
		
		//REIMPRESSÃO
		if (tipoImpressao.equals(EnumTipoImpressao.REIMPRESSAO)){
			relSolHemoterapicaVO.setTipo("**  REIMPRESSÃO  **");
		} else {
			//PRIMEIRA IMPRESSÃO 
			relSolHemoterapicaVO.setTipo("**  SOLICITAÇÃO  **");
		}
		if (!statusItem.equals(EnumStatusItem.VELHO)
				&& hemoterapia.getDthrFim() == null) {
			listaRelSolHemoterapicaVO.add(relSolHemoterapicaVO);					
		}
	}
	
	/**
	 * Método que providencia a exibição das movimentações das hemoterapias
	 * 
	 * @param statusItem
	 * @param relSolHemoterapicaVO
	 * @param listaRelSolHemoterapicaVO
	 */
	protected void providenciarSomenteMovimentacoes(EnumStatusItem statusItem,
			RelSolHemoterapicaVO relSolHemoterapicaVO,
			List<RelSolHemoterapicaVO> listaRelSolHemoterapicaVO) {
		
		switch(statusItem){
		case NENHUMA:
			break;
		case INCLUIDO:
			relSolHemoterapicaVO.setTipo("####  INCLUSÃO  ####");
			listaRelSolHemoterapicaVO.add(relSolHemoterapicaVO);
			break;
		case ALTERADO:
			relSolHemoterapicaVO.setTipo("####  ALTERAÇÃO  ####");
			listaRelSolHemoterapicaVO.add(relSolHemoterapicaVO);
			break;
		case EXCLUIDO:
			relSolHemoterapicaVO.setTipo("####  EXCLUSÃO  ####");
			listaRelSolHemoterapicaVO.add(relSolHemoterapicaVO);
			break;
		case VELHO:
			break;
		}
		
	}
	
	/**
	 * Método que verifica se foi solicitado uma impressão ou reimpressão
	 * 
	 * @param tipoImpressao
	 * @return
	 */
	protected Boolean verificarReimpressao(EnumTipoImpressao tipoImpressao, RapServidores servidorValida){
		PrescricaoMedicaON prescricaoMedicaON = this.getPrescricaoMedicaON();
		Boolean retorno = false;
		if (tipoImpressao.equals(EnumTipoImpressao.IMPRESSAO)
				|| tipoImpressao.equals(EnumTipoImpressao.SEM_IMPRESSAO)) {
			retorno = prescricaoMedicaON.verificarPrimeiraImpressao(servidorValida);			
		} else if (tipoImpressao.equals(EnumTipoImpressao.REIMPRESSAO)) {
			retorno = true;	
		}
		return retorno;
	}
	
	/**
	 * Popula o objeto VO com valores da prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @param relSolHemoterapicaVO
	 */
	protected void atribuirValoresPrescricao(
			MpmPrescricaoMedica prescricaoMedica,
			RelSolHemoterapicaVO relSolHemoterapicaVO) {
		relSolHemoterapicaVO.setPacienteProntuario(prescricaoMedica
				.getAtendimento().getPaciente().getProntuario());
		relSolHemoterapicaVO.setPacienteNome(prescricaoMedica.getAtendimento()
				.getPaciente().getNome());
		relSolHemoterapicaVO.setPrescricao(prescricaoMedica.getId().getSeq());
		relSolHemoterapicaVO.setPacienteSexo(prescricaoMedica.getAtendimento()
				.getPaciente().getSexo().toString());
		relSolHemoterapicaVO.setDataNascimento(prescricaoMedica
				.getAtendimento().getPaciente().getDtNascimento());
	}
	
	/**
	 * Popula o objeto VO com valores da hemoterapia
	 * 
	 * @param hemoterapia
	 * @param relSolHemoterapicaVO
	 */
	private void atribuirValoresHemoterapia(
			AbsSolicitacoesHemoterapicas hemoterapia,
			RelSolHemoterapicaVO relSolHemoterapicaVO) {
		
		relSolHemoterapicaVO.setDataHora(hemoterapia.getCriadoEm());
		this.atribuirSituacaoColeta(hemoterapia, relSolHemoterapicaVO);

		relSolHemoterapicaVO.setTransfusoesUltimos3Dias(hemoterapia
				.getIndTransfAnteriores());
		relSolHemoterapicaVO.setPacienteTransplantado(hemoterapia
				.getIndPacTransplantado());

		this.verificarAtribuirObservacao(hemoterapia, relSolHemoterapicaVO);

		this.verificarIndUrgente(hemoterapia, relSolHemoterapicaVO);

		this.popularLocalizacaoPaciente(relSolHemoterapicaVO, hemoterapia);
		relSolHemoterapicaVO.setConvenio(this
				.buscarConvenioPaciente(hemoterapia));
	}
	
		
	/**
	 * Atribui a situação de coleta ao objeto VO de hemoterapia
	 * 
	 * @param hemoterapia
	 * @param relSolHemoterapicaVO
	 */
	protected void atribuirSituacaoColeta(
			AbsSolicitacoesHemoterapicas hemoterapia,
			RelSolHemoterapicaVO relSolHemoterapicaVO) {
		
		relSolHemoterapicaVO.setSituacaoAmostra("");
		if (hemoterapia.getIndSituacaoColeta() != null){
			switch(hemoterapia.getIndSituacaoColeta()){
			case P:
				if (DominioResponsavelColeta.C.equals(hemoterapia
						.getIndResponsavelColeta())) {
					relSolHemoterapicaVO
							.setSituacaoAmostra("Será coletada pela coleta");
				}
				break;
			case E:
				if (DominioResponsavelColeta.S.equals(hemoterapia
						.getIndResponsavelColeta())) {
					relSolHemoterapicaVO
							.setSituacaoAmostra("Será coletada pelo solicitante");
				}
				break;
			case D:
				if (DominioResponsavelColeta.N.equals(hemoterapia
						.getIndResponsavelColeta())) {
					relSolHemoterapicaVO
							.setSituacaoAmostra("Existe amostra válida no Banco de Sangue");
				}
				break;
			}
		}
		}
		
	/**
	 * Verifica se deve atribuir o índice de urgência
	 * 
	 * @param hemoterapia
	 * @param relSolHemoterapicaVO
	 */
	protected void verificarIndUrgente(
			AbsSolicitacoesHemoterapicas hemoterapia,
			RelSolHemoterapicaVO relSolHemoterapicaVO) {
		if (hemoterapia.getIndUrgente() != null && hemoterapia.getIndUrgente()){
			relSolHemoterapicaVO.setUrgente("Urgente");
		}
	}
		
	/**
	 * Verifica se deve atribuir a observação
	 * 
	 * @param hemoterapia
	 * @param relSolHemoterapicaVO
	 */
	protected void verificarAtribuirObservacao(
			AbsSolicitacoesHemoterapicas hemoterapia,
			RelSolHemoterapicaVO relSolHemoterapicaVO) {
		if (StringUtils.isNotBlank(hemoterapia.getObservacao())) {
			relSolHemoterapicaVO.setObservacao(hemoterapia.getObservacao());
		}
	}
	
	/**
	 * Busca o local onde o paciente está internado
	 * 
	 * @param hemoterapia
	 * @return
	 */
	protected void popularLocalizacaoPaciente(RelSolHemoterapicaVO relSolHemoterapicaVO,AbsSolicitacoesHemoterapicas hemoterapia) {
		if (hemoterapia.getPrescricaoMedica().getAtendimento().getLeito() != null) {
			relSolHemoterapicaVO.setLabelLocalizacao("Leito");
			relSolHemoterapicaVO.setLocalizacao(hemoterapia.getPrescricaoMedica().getAtendimento().getLeito().getLeitoID());
		} else if (hemoterapia.getPrescricaoMedica().getAtendimento().getQuarto() != null) {
			relSolHemoterapicaVO.setLabelLocalizacao("Quarto");
			relSolHemoterapicaVO.setLocalizacao(hemoterapia.getPrescricaoMedica().getAtendimento().getQuarto().getDescricao());
		} else {
			if (hemoterapia.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getSigla() != null) {
				relSolHemoterapicaVO.setLabelLocalizacao("Unidade");
				relSolHemoterapicaVO.setLocalizacao(hemoterapia.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getSigla());
			} else {
				String andar = hemoterapia.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getAndar();
				AghAla ala = hemoterapia.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getIndAla();
				if (StringUtils.isNotBlank(andar) || ala != null) {
					relSolHemoterapicaVO.setLabelLocalizacao("Unidade");
					relSolHemoterapicaVO.setLocalizacao(andar + " " + ala.toString());
				}
			}
		}
	}
	
	/**
	 * Busca o convênio do paciente internado TODO: RETIRAR OS REFRESHS QUANDO
	 * AS EXCEPTIONS COM ROLLBACK DOS CRUDS DOS ÍTENS DA PRESCRIÇÃO TIVEREM SIDO
	 * ELIMINADAS
	 * 
	 * @param hemoterapia
	 * @return
	 */
	protected String buscarConvenioPaciente(
			AbsSolicitacoesHemoterapicas hemoterapia) {
		String retorno = null;
		AghAtendimentos atendimento = hemoterapia.getPrescricaoMedica()
				.getAtendimento();
		
		if (atendimento != null){
			if (atendimento.getInternacao() != null){
				super.refresh(atendimento.getInternacao());
				if (atendimento.getInternacao().getConvenioSaude() != null){
					retorno = atendimento.getInternacao().getConvenioSaude()
							.getDescricao();
				}
			} else if (atendimento.getAtendimentoUrgencia() != null) {
				super.refresh(atendimento.getAtendimentoUrgencia());
				if (atendimento.getAtendimentoUrgencia().getConvenioSaude() != null){
					retorno = atendimento.getAtendimentoUrgencia()
							.getConvenioSaude().getDescricao();
				}
			} else if (atendimento.getHospitalDia() != null) {
				super.refresh(atendimento.getHospitalDia());
				if (atendimento.getHospitalDia().getConvenioSaude() != null){
					retorno = atendimento.getHospitalDia().getConvenioSaude()
							.getDescricao();
				}
			}
		}
		
		return retorno;
	}
	
	/**
	 * Método que retorna os ítens Componentes Sanguíneos de Hemoterapia que
	 * serão utilizados no relatório de hemoterapias
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	protected List<RelItemComponenteSangSolHemoterapicaVO> pesquisarItensComponentesSanguineosRel(
			AbsSolicitacoesHemoterapicas hemoterapia) {
		
		List<AbsItensSolHemoterapicas> listaItensCompSang = getBancoDeSangueFacade()
				.pesquisarItensHemoterapiaComponentesSanguineos(hemoterapia
						.getPrescricaoMedica().getAtendimento().getSeq(),
						hemoterapia.getId().getSeq());
		
		List<RelItemComponenteSangSolHemoterapicaVO> listaVOs = new ArrayList<RelItemComponenteSangSolHemoterapicaVO>();
		
		for (AbsItensSolHemoterapicas itemComponenteSanguineo: listaItensCompSang){
			RelItemComponenteSangSolHemoterapicaVO itemCompSangVO = new RelItemComponenteSangSolHemoterapicaVO();
			this.atribuirValoresComponentesSanguineos(itemComponenteSanguineo,
					itemCompSangVO);
			
			//Seta a lista de Justificativas
			itemCompSangVO.setListaJustificativas(this
					.pesquisarJustificativasItens(itemComponenteSanguineo));
			
			listaVOs.add(itemCompSangVO);
		}
		
		return listaVOs;
	}
	
	/**
	 * Método que retorna os ítens Procedimentos de Hemoterapia que serão
	 * utilizados no relatório de hemoterapias
	 * 
	 * @param hemoterapia
	 * @return
	 */
	protected List<RelItemProcedimentoHemoSolHemoterapicaVO> pesquisarItensProcedimentosRel(
			AbsSolicitacoesHemoterapicas hemoterapia) {
		List<AbsItensSolHemoterapicas> listaItensProcedimentos = getBancoDeSangueFacade()
				.pesquisarItensHemoterapiaProcedimentos(hemoterapia
						.getPrescricaoMedica().getAtendimento().getSeq(),
						hemoterapia.getId().getSeq());
		
		List<RelItemProcedimentoHemoSolHemoterapicaVO> listaVOs = new ArrayList<RelItemProcedimentoHemoSolHemoterapicaVO>();
		
		for (AbsItensSolHemoterapicas itemProcedimento: listaItensProcedimentos){
			RelItemProcedimentoHemoSolHemoterapicaVO itemProcedSangVO = new RelItemProcedimentoHemoSolHemoterapicaVO();
			itemProcedSangVO.setDescricao(itemProcedimento.getDescricao());
			//Seta as Justificativas
			itemProcedSangVO.setListaJustificativas(this
					.pesquisarJustificativasItens(itemProcedimento));
			
			listaVOs.add(itemProcedSangVO);
		}
		
		return listaVOs;
	}

	/**
	 * Pesquisa pelo Cids do Atendimento
	 * 
	 * @param atendimento
	 * @return
	 */
	protected List<RelAtendimentoCidSolHemoterapicaVO> pesquisarCidsAtendimento(
			AghAtendimentos atendimento) {
		MpmCidAtendimentoDAO mpmCidAtendimentoDAO = getMpmCidAtendimentoDAO();
		List<MpmCidAtendimento> listaCidsAtendimento = mpmCidAtendimentoDAO
				.listar(atendimento);
		List<RelAtendimentoCidSolHemoterapicaVO> listaAtendimentos = new ArrayList<RelAtendimentoCidSolHemoterapicaVO>();
		for (MpmCidAtendimento cidAtendimento: listaCidsAtendimento){
			AghCid cid = cidAtendimento.getCid();
			AghCid cid1 = cidAtendimento.getCid().getCid();
			StringBuffer descricao = new StringBuffer();
			if (cid1 != null){
				descricao.append("      ").append(cid1.getDescricao()).append("      ");
				RelAtendimentoCidSolHemoterapicaVO cidAtendimentoVO = new RelAtendimentoCidSolHemoterapicaVO();
				cidAtendimentoVO.setDescricao(descricao.toString());
				listaAtendimentos.add(cidAtendimentoVO);
				descricao = new StringBuffer();
			}
			/*--Acerta os espaços do código--*/
			StringBuffer codigoCid = new StringBuffer(cid.getCodigo());
			while (codigoCid.length() < 5){
				codigoCid.append(' ');
			}
			/* ---------------------*/
			descricao.append(codigoCid).append(' ').append(cid.getDescricao());
			
			if (StringUtils.isNotBlank(cidAtendimento.getComplemento())){
				descricao.append(" - ").append(cidAtendimento.getComplemento()); 
			} else {
				descricao.append(' ');
			}
			RelAtendimentoCidSolHemoterapicaVO cidAtendimentoVO = new RelAtendimentoCidSolHemoterapicaVO();
			cidAtendimentoVO.setDescricao(descricao.toString());
			listaAtendimentos.add(cidAtendimentoVO);
		}

		return listaAtendimentos;
	}


	
	/**
	 * Método que pesquisa as justificativas dos ítens de hemoterapia
	 * 
	 * @param itemComponenteSanguineo
	 * @return
	 */
	protected List<RelSolHemoterapicasJustificativaVO> pesquisarJustificativasItens(
			AbsItensSolHemoterapicas itemComponenteSanguineo) {
		
		List<AbsItemSolicitacaoHemoterapicaJustificativa> listaJustificativas = getBancoDeSangueFacade()
				.pesquisarJustificativasItemHemoterapia(itemComponenteSanguineo
						.getId().getSheAtdSeq(), itemComponenteSanguineo
						.getId().getSheSeq(), itemComponenteSanguineo.getId()
						.getSequencia());
		
		List<RelSolHemoterapicasJustificativaVO> listaComponenteJust = new ArrayList<RelSolHemoterapicasJustificativaVO>();
	
		for (AbsItemSolicitacaoHemoterapicaJustificativa justificativa: listaJustificativas){
			//TODO: REVER ESTE REFRESH
			getBancoDeSangueFacade().refreshAbsItemSolicitacaoHemoterapicaJustificativa(justificativa);
			String descricaoGrupo = justificativa
						.getJustificativaComponenteSanguineo()
					.getGrupoJustificativaComponenteSanguineo().getDescricao();
			
			RelSolHemoterapicasJustificativaVO componenteJust = new RelSolHemoterapicasJustificativaVO();
			StringBuffer descricaoJustificativa = new StringBuffer(justificativa
					.getJustificativaComponenteSanguineo().getDescricao());
			if (StringUtils.isNotBlank(justificativa.getDescricaoLivre())){
				descricaoJustificativa.append(". ")
						.append(justificativa.getDescricaoLivre());
			}
			componenteJust.setDescricao(descricaoJustificativa.toString());
			componenteJust.setGrupoDescricao(descricaoGrupo);
			listaComponenteJust.add(componenteJust);
		}
		
		return listaComponenteJust;
	}
	
	
	
	
	
	/**
	 * Método que atribui os valores dos grupos sanguíneos
	 * 
	 * @param itemGrupoSanguineo
	 * @param itemGrupoSangVO
	 */
	private void atribuirValoresComponentesSanguineos(
			AbsItensSolHemoterapicas itemCompSanguineo,
			RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO) {
		
		itemGrupoSangVO.setDescricao(itemCompSanguineo
				.getDescricaoComIndicadores());

		this.atribuirQuantidadeItemComponenteSanguineo(itemCompSanguineo,
				itemGrupoSangVO);
		this.atribuirAprazamentoItemComponenteSanguineo(itemCompSanguineo,
				itemGrupoSangVO);
		this.atribuirQuantidadeAplicacoes(itemCompSanguineo, itemGrupoSangVO);
		
		itemGrupoSangVO.setAtdSeqHemoterapia(itemCompSanguineo.getId()
				.getSheAtdSeq());
		itemGrupoSangVO
				.setSeqHemoterapia(itemCompSanguineo.getId().getSheSeq());
		itemGrupoSangVO.setSequencia(itemCompSanguineo.getId().getSequencia()
				.intValue());
	}

	
	/**
	 * Atribui a o valor da quantidade ao item de componente sangüíneo
	 * 
	 * @param itemCompSanguineo
	 * @param itemGrupoSangVO
	 */
	protected void atribuirQuantidadeItemComponenteSanguineo(
			AbsItensSolHemoterapicas itemCompSanguineo,
			RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO){
		
		if (itemCompSanguineo.getQtdeUnidades() == null){
			if (itemCompSanguineo.getQtdeMl() != null){
				itemGrupoSangVO.setQuantidade(itemCompSanguineo.getQtdeMl()
						+ " ml");
			} else {
				itemGrupoSangVO.setQuantidade("");
			}
		} else {
			itemGrupoSangVO.setQuantidade(itemCompSanguineo.getQtdeUnidades()
					+ " un");
		}
		}
			
	/**
	 * Atribui a o valor do aprazamento ao item de componente sangüíneo
	 * 
	 * @param itemCompSanguineo
	 * @param itemGrupoSangVO
	 */
	protected void atribuirAprazamentoItemComponenteSanguineo(
			AbsItensSolHemoterapicas itemCompSanguineo,
			RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO){
		
		if (itemCompSanguineo.getTipoFreqAprazamento() != null) {
			if (itemCompSanguineo.getTipoFreqAprazamento().getSintaxe() != null
					&& itemCompSanguineo.getFrequencia() != null) {
				String sintaxe = itemCompSanguineo.getTipoFreqAprazamento()
						.getSintaxe();
				String frequencia = itemCompSanguineo.getFrequencia()
						.toString();
				itemGrupoSangVO.setAprazamento(obterAprazamento(sintaxe,
						frequencia));
			} else {
				itemGrupoSangVO.setAprazamento(itemCompSanguineo
						.getTipoFreqAprazamento().getDescricao());
			}
		} else {
			itemGrupoSangVO.setAprazamento("");
			}
		}

	/**
	 * Atribui o valor da quantidade de aplicações ao item de componente
	 * sangüíneo
	 * 
	 * @param itemCompSanguineo
	 * @param itemGrupoSangVO
	 */
	protected void atribuirQuantidadeAplicacoes(
			AbsItensSolHemoterapicas itemCompSanguineo,
			RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO){
		
		if (itemCompSanguineo.getQtdeAplicacoes() != null){
			itemGrupoSangVO.setQuantidadeAplicacoes(itemCompSanguineo
					.getQtdeAplicacoes().toString());
		} else {
			itemGrupoSangVO.setQuantidadeAplicacoes("");
		}
	}
	
	/**
	 * Obtém o aprazamento correto
	 * 
	 * @param sintaxe
	 * @param frequencia
	 * @return
	 */
	protected String obterAprazamento(String sintaxe, String frequencia) {
		StringBuffer retorno = new StringBuffer();
		for (int i=0;i<sintaxe.length();i++){
			if (sintaxe.charAt(i) == '#'){
				retorno.append(frequencia);
			} else {
				retorno.append(sintaxe.charAt(i));
			}
		}
		
		return retorno.toString();
	}
	
	public List<CompSanguineoProcedHemoterapicoVO> pesquisarCompSanguineoProcedHemoterapico() {

		List<CompSanguineoProcedHemoterapicoVO> itensListaVO = new ArrayList<CompSanguineoProcedHemoterapicoVO>();

		List<AbsComponenteSanguineo> componentesSanguineos = getBancoDeSangueFacade()
				.obterComponetesSanguineosAtivos();

		for (AbsComponenteSanguineo componenteSanguineo : componentesSanguineos) {
			CompSanguineoProcedHemoterapicoVO itemVO = new CompSanguineoProcedHemoterapicoVO();

			itemVO.setCodigo(componenteSanguineo.getCodigo());
			itemVO.setCodigoComposto(CompSanguineoProcedHemoterapicoVO.DominioTipo.C
							+ componenteSanguineo.getCodigo());
			itemVO.setTipo(CompSanguineoProcedHemoterapicoVO.DominioTipo.C);
			itemVO.setDescricao(componenteSanguineo.getDescricao());
			itemVO.setIndAferese(componenteSanguineo.getIndAferese());
			itemVO.setIndFiltrado(componenteSanguineo.getIndFiltrado());
			itemVO.setIndIrradiado(componenteSanguineo.getIndIrradiado());
			itemVO.setIndLavado(componenteSanguineo.getIndLavado());
			itemVO.setIndJustificativa(componenteSanguineo
					.getIndJustificativa());
			itemVO.setAvisoPrescricao(componenteSanguineo.getAvisoPrescricao());
			itemVO.setAvisoAferese(componenteSanguineo.getMensSolicAferese());

			itensListaVO.add(itemVO);
		}

		List<AbsProcedHemoterapico> procedHemoterapicos = getBancoDeSangueFacade()
				.obterProcedHemoterapicosAtivos();

		for (AbsProcedHemoterapico procedHemoterapico : procedHemoterapicos) {
			CompSanguineoProcedHemoterapicoVO itemVO = new CompSanguineoProcedHemoterapicoVO();

			itemVO.setCodigo(procedHemoterapico.getCodigo());
			itemVO.setCodigoComposto(CompSanguineoProcedHemoterapicoVO.DominioTipo.P
							+ procedHemoterapico.getCodigo());
			itemVO.setTipo(CompSanguineoProcedHemoterapicoVO.DominioTipo.P);
			itemVO.setDescricao(procedHemoterapico.getDescricao());
			itemVO.setIndAferese(false);
			itemVO.setIndFiltrado(false);
			itemVO.setIndIrradiado(false);
			itemVO.setIndLavado(false);
			itemVO.setIndJustificativa(procedHemoterapico.getIndJustificativa());
			itensListaVO.add(itemVO);
		}
		Collections.sort(itensListaVO,
				COMP_SANGUINEO_PROCED_HEMOTERAPICO_COMPARATOR);
		return itensListaVO;
	}

	public List<AbsSolicitacoesHemoterapicas> obterListaSolicitacoesHemoterapicasPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricaoId) {
		List<AbsSolicitacoesHemoterapicas> listaHemoterapias = getBancoDeSangueFacade()
				.pesquisarTodasHemoterapiasPrescricaoMedica(prescricaoId);
		for (AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas : listaHemoterapias) {
			absSolicitacoesHemoterapicas.getDescricaoFormatada();
		}
		return listaHemoterapias;
	}
	
	/**
	 * Recebe um MAP contendo uma lista de justificativasVO para cada grupo de
	 * justificativas, onde o Id do grupo é chave no MAP.
	 * 
	 * @param justificativasSelecionadas
	 */
	public List<AbsItemSolicitacaoHemoterapicaJustificativa> gravarItemSolicitacaoHemoterapicaJustificativa(Map<Short, List<JustificativaComponenteSanguineoVO>> justificativasSelecionadas)
		throws ApplicationBusinessException {
		List<AbsItemSolicitacaoHemoterapicaJustificativa> listaReturn = new LinkedList<>();
		
		boolean indMarcado = false;
		List<JustificativaComponenteSanguineoVO> listaParaPersistencia = new ArrayList<JustificativaComponenteSanguineoVO>();

		if (justificativasSelecionadas != null && !justificativasSelecionadas.isEmpty()) {
			List<List<JustificativaComponenteSanguineoVO>> listaDeLista = 
					new ArrayList<List<JustificativaComponenteSanguineoVO>>(justificativasSelecionadas.values());
			
			// Verifica se pelo menos uma justificativa foi selecionada.
			for (List<JustificativaComponenteSanguineoVO> listaDeVO : listaDeLista) {
				for (JustificativaComponenteSanguineoVO justificativaComponenteSanguineoVO : listaDeVO) {
					if (justificativaComponenteSanguineoVO.getMarcado() == true) {
						indMarcado = true;
					} else {
						justificativaComponenteSanguineoVO.setDescricaoLivre(null);
					}
					listaParaPersistencia.add(justificativaComponenteSanguineoVO);
				}
			}
			
			if (indMarcado) {
				for (JustificativaComponenteSanguineoVO vo : listaParaPersistencia) {
					AbsItemSolicitacaoHemoterapicaJustificativa item = vo.getItemSolicitacaoHemoterapicaJustificativa();
					
					item.setMarcado(vo.getMarcado());
					item.setDescricaoLivre(vo.getDescricaoLivre());
					
					this.getBancoDeSangueFacade().atualizarJustificativaItemSolicitacaoHemoterapica(item);
					listaReturn.add(item);
					super.flush();
				}
			} else {
				throw new ApplicationBusinessException(SolicitacaoHemoterapicaONExceptionCode.ERRO_SOLICITACAO_HEMOTERAPICA_SEM_JUSTIFICATIVA);
			}
			
		}//if justificativasSelecionadas
		
		return listaReturn;
	}
	
	public AbsSolicitacoesHemoterapicas clonarSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica)
			throws ApplicationBusinessException {
		try {
			
			AbsSolicitacoesHemoterapicas novaSolicitacaoHemoterapica = (AbsSolicitacoesHemoterapicas) SerializationHelper
					.clone(solicitacaoHemoterapica);
			novaSolicitacaoHemoterapica
					.setId(new AbsSolicitacoesHemoterapicasId(
							solicitacaoHemoterapica.getPrescricaoMedica()
									.getId().getAtdSeq(), null));
			novaSolicitacaoHemoterapica
					.setIndPendente(DominioIndPendenteItemPrescricao.P);
			novaSolicitacaoHemoterapica
					.setSolicitacaoHemoterapica(solicitacaoHemoterapica);
			novaSolicitacaoHemoterapica
					.setPrescricaoMedica(solicitacaoHemoterapica
							.getPrescricaoMedica());
			novaSolicitacaoHemoterapica.setServidorMovimentado(null);
			novaSolicitacaoHemoterapica.setServidor(null);
			novaSolicitacaoHemoterapica.setServidorValidacao(null);
			novaSolicitacaoHemoterapica.setDthrValida(null);
			if(solicitacaoHemoterapica.getItensSolHemoterapicas() != null) {
				novaSolicitacaoHemoterapica
						.setItensSolHemoterapicas(new ArrayList<AbsItensSolHemoterapicas>(
								0));
				for (AbsItensSolHemoterapicas itemSolHem : solicitacaoHemoterapica
						.getItensSolHemoterapicas()) {
					AbsItensSolHemoterapicas novoItemSolHem = (AbsItensSolHemoterapicas) SerializationHelper
							.clone(itemSolHem);
					novoItemSolHem.setId(new AbsItensSolHemoterapicasId(
							itemSolHem.getId().getSheAtdSeq(), null, null));
					novoItemSolHem
							.setSolicitacaoHemoterapica(novaSolicitacaoHemoterapica);
					novoItemSolHem.setProcedHemoterapico(itemSolHem
							.getProcedHemoterapico());
					novoItemSolHem.setComponenteSanguineo(itemSolHem
							.getComponenteSanguineo());
					novoItemSolHem.setTipoFreqAprazamento(itemSolHem
							.getTipoFreqAprazamento());
					novoItemSolHem.setServidor(itemSolHem.getServidor());
					if (itemSolHem
							.getItemSolicitacaoHemoterapicaJustificativas() != null) {
						novoItemSolHem
								.setItemSolicitacaoHemoterapicaJustificativas(new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(
										0));
						for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolHemJust : itemSolHem
								.getItemSolicitacaoHemoterapicaJustificativas()) {
							AbsItemSolicitacaoHemoterapicaJustificativa novoItemSolHemJust = (AbsItemSolicitacaoHemoterapicaJustificativa) SerializationHelper
									.clone(itemSolHemJust);
							novoItemSolHemJust
									.setId(new AbsItemSolicitacaoHemoterapicaJustificativaId(
											itemSolHem.getId().getSheAtdSeq(),
											null,
											null,
											itemSolHemJust
													.getJustificativaComponenteSanguineo()
													.getSeq()));
							novoItemSolHemJust
									.setItemSolucaoHemoterapica(novoItemSolHem);
							novoItemSolHem
									.getItemSolicitacaoHemoterapicaJustificativas()
									.add(novoItemSolHemJust);
						}
					}
					novaSolicitacaoHemoterapica.getItensSolHemoterapicas().add(
							novoItemSolHem);
				}
			}
			return novaSolicitacaoHemoterapica;
			
		} catch( Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(null);
		}
	}
	
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamentoHemoterapico(
			String strPesquisa) {
		return getMpmTipoFrequenciaAprazamentoDAO()
				.obterListaTipoFrequenciaAprazamentoHemoterapico(strPesquisa);
	}
	
	protected PrescricaoMedicaON getPrescricaoMedicaON() {
		return prescricaoMedicaON;
	}
	
	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO(){
		return mpmCidAtendimentoDAO;
	}
	
	protected LaudoProcedimentoSusRN getLaudoProcedimentoSusRN() {
		return laudoProcedimentoSusRN;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return this.bancoDeSangueFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}