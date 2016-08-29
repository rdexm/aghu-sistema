package br.gov.mec.aghu.exames.solicitacao.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.ImprimirExamesRealizadosAtendimentosDiversosVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ImprimirExamesRealizadosAtendimentosDiversosON extends BaseBusiness {


@EJB
private SolicitacaoExameRN solicitacaoExameRN;

private static final Log LOG = LogFactory.getLog(ImprimirExamesRealizadosAtendimentosDiversosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

@Inject
private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 518395152134222484L;

	public List<ImprimirExamesRealizadosAtendimentosDiversosVO> imprimirExamesRealizadosAtendimentosDiversos(
			Date dataInicial, Date dataFinal, DominioSimNao grupoSus,
			FatConvenioSaude convenioSaude) throws ApplicationBusinessException {
		// TODO Auto-generated method stub
		List<ImprimirExamesRealizadosAtendimentosDiversosVO> listaVO = new ArrayList<ImprimirExamesRealizadosAtendimentosDiversosVO>();
		List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().pesquisarExamesRealizadosAtendimentosDiversos(dataInicial, dataFinal,grupoSus, convenioSaude);
		
		for(AelItemSolicitacaoExames item : itens){
			ImprimirExamesRealizadosAtendimentosDiversosVO vo = new ImprimirExamesRealizadosAtendimentosDiversosVO();
			vo.setGrupo(item.getSolicitacaoExame().getConvenioSaude().getGrupoConvenio().getDescricao());
			vo.setCodConvenio(item.getSolicitacaoExame().getConvenioSaude().getCodigo().toString());
			vo.setDescrConvenio(item.getSolicitacaoExame().getConvenioSaude().getDescricao());
			
			String nomeResponsavel = getSolicitacaoExameRN().buscarResponsavelProjetoPesquisaPorSolicitacaoExame(item.getSolicitacaoExame().getSeq());
			String laudoNomePac = getExamesFacade().buscarLaudoNomePaciente(item.getSolicitacaoExame());
			if(nomeResponsavel != null){
				StringBuilder sb1 = new StringBuilder();
				sb1.append("Resp. ").append(nomeResponsavel).append(" - ").append(laudoNomePac);
				vo.setNome(sb1.toString());
			}else{
				vo.setNome(laudoNomePac);
			}
			
			vo.setProjeto(laudoNomePac);
			vo.setSoeSeq(item.getSolicitacaoExame().getSeq().toString());
			vo.setNomeExame(item.getExameMatAnalise().getNomeUsualMaterial());
			if(DominioSimNao.S.equals(grupoSus)){				
				StringBuilder sb1 = getIFaturamentoFacade().buscarPhiSus(item);
				vo.setPhi(sb1.toString());
			}
			
			BigDecimal valor = null;
			if(DominioSimNao.S.equals(grupoSus)){
				//Aelc_vlr_exame_sus(Trunc(ise.dthr_liberada),Trunc(ise.dthr_liberada + 1), ISE.ufe_ema_exa_sigla,ISE.ufe_ema_man_seq)
				Date dataLiberadaMaisUm = DateUtils.addDays(item.getDthrLiberada(), 1);
				valor = obterValorExameSus(item.getDthrLiberada(),dataLiberadaMaisUm, item.getExameMatAnalise().getId().getExaSigla(),item.getExameMatAnalise().getId().getManSeq()); 
				
			}else if(DominioSimNao.N.equals(grupoSus)){
				//Ffcc_valor_exame(ISE.ufe_ema_exa_sigla, ISE.ufe_ema_man_seq,ISE.ufe_unf_seq,Trunc(ISE.dthr_liberada), SOE.csp_cnv_codigo, SOE.csp_seq)
				valor = obterValorExame(item.getExameMatAnalise().getId().getExaSigla(),item.getExameMatAnalise().getId().getManSeq(),item.getUnidadeFuncional(),item.getDthrLiberada(),item.getSolicitacaoExame().getConvenioSaudePlano());
			}
			
			vo.setValor(valor.doubleValue());
			vo.setQtde(1); //TODO: REVISAR ISSO
			listaVO.add(vo);
		}
		
		orderListaVO(listaVO);
		
		Integer count = 0;
		String nome = "";
		String codConvenio = "";
		for(ImprimirExamesRealizadosAtendimentosDiversosVO vo: listaVO){
			if(!nome.equals(vo.getNome())){
				if(!codConvenio.equals(vo.getCodConvenio())){
					count = 0;
					codConvenio = vo.getCodConvenio();
				}
				count++;
				nome = vo.getNome();
				vo.setSeq(count);
			}else{
				vo.setSeq(null);
				vo.setNome(null);
			}
			
		}
		return listaVO;
	}


	private void orderListaVO(
			List<ImprimirExamesRealizadosAtendimentosDiversosVO> listaVO) {
		Collections.sort(listaVO,  new Comparator<ImprimirExamesRealizadosAtendimentosDiversosVO>() {
			@Override
			public int compare(ImprimirExamesRealizadosAtendimentosDiversosVO o1, ImprimirExamesRealizadosAtendimentosDiversosVO o2) {
				int compare = o1.getGrupo().compareTo(o2.getGrupo());
				if (compare==0){
					if(o1.getCodConvenio() != null && o2.getCodConvenio() != null){
						compare =  o1.getCodConvenio().compareTo(o2.getCodConvenio());
						if(compare==0){
							if(o1.getDescrConvenio() != null && o2.getDescrConvenio() != null){
								compare =  o1.getDescrConvenio().compareTo(o2.getDescrConvenio());
								if(compare==0){
									if(o1.getNome() != null && o2.getNome() != null){
										compare =  o1.getNome().compareTo(o2.getNome());
										if(compare==0){
											if(o1.getProjeto() != null && o2.getProjeto() != null){
												compare =  o1.getProjeto().compareTo(o2.getProjeto());
												if(compare==0){
													if(o1.getSoeSeq() != null && o2.getSoeSeq() != null){
														compare =  o1.getSoeSeq().compareTo(o2.getSoeSeq());
														if(compare==0){
															if(o1.getNomeExame() != null && o2.getNomeExame() != null){
																compare =  o1.getNomeExame().compareTo(o2.getNomeExame());
																if(compare==0){
																	if(o1.getPhi() != null && o2.getPhi() != null){
																		compare =  o1.getPhi().compareTo(o2.getPhi());
																		if(compare==0){
																			if(o1.getValor() != null && o2.getValor() != null){
																				compare =  o1.getValor().compareTo(o2.getValor());
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				return compare;
			}
		});
	}

	
	/**
	 * @ORADB CONV.FFCC_VALOR_EXAME
	 * @param exaSigla
	 * @param manSeq
	 * @param unidadeFuncional
	 * @param dthrLiberada
	 * @param convenioSaudePlano
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private BigDecimal obterValorExame(String exaSigla, Integer manSeq,
			AghUnidadesFuncionais unidadeFuncional, Date dthrLiberada,
			FatConvenioSaudePlano convenioSaudePlano) throws ApplicationBusinessException {
		final Boolean isOracle = aelAgrpPesquisasDAO.isOracle();
		Boolean tipoConv = getAelItemSolicitacaoExameDAO().verificarTipoxConvPorConvCod(convenioSaudePlano.getId().getCnvCodigo(),isOracle);
		BigDecimal valor = BigDecimal.ZERO;
		if(tipoConv){
			valor = obterValorExameSus(dthrLiberada,new Date(), exaSigla,manSeq);
		}else{
			valor = getAelAgrpPesquisasDAO().obterValorExame(exaSigla,manSeq,unidadeFuncional,dthrLiberada,convenioSaudePlano,isOracle);
		}
		return valor;
	}


	/**
	 * @ORADB aelc_vlr_exame_sus
	 * @param exaSigla
	 * @param manSeq
	 * @param unidadeFuncional
	 * @param dthrLiberada
	 * @param convenioSaudePlano
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private BigDecimal obterValorExameSus(Date dthrLiberada, Date dataFinalMaisUm, String exaSigla, Integer manSeq) throws ApplicationBusinessException {
		AghParametros pTipoGrupoContaSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS); 
		List<FatVlrItemProcedHospComps> listaItem = getIFaturamentoFacade().pesquisarVlrItemProcedHospCompsPorDthrLiberadaExaSiglaManSeq(dthrLiberada,dataFinalMaisUm,exaSigla,manSeq,pTipoGrupoContaSus);
		BigDecimal valor = BigDecimal.ZERO;
		if(listaItem != null && !listaItem.isEmpty()){
			FatVlrItemProcedHospComps vlrItem = listaItem.iterator().next();
			if(vlrItem.getVlrServHospitalar() != null) {
				valor = valor.add(vlrItem.getVlrServHospitalar());
			}
			if(vlrItem.getVlrServProfissional() != null) {
				valor = valor.add(vlrItem.getVlrServProfissional());
			}
			if(vlrItem.getVlrSadt() != null) {
				valor = valor.add(vlrItem.getVlrSadt());
			}
			if(vlrItem.getVlrProcedimento() != null) {
				valor = valor.add(vlrItem.getVlrProcedimento());
			}
			if(vlrItem.getVlrAnestesista() != null) {
				valor = valor.add(vlrItem.getVlrAnestesista());
			}
		}
		
		return valor;
	}



	public SolicitacaoExameRN getSolicitacaoExameRN(){
		return solicitacaoExameRN;
	}
	
	public AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}

	private IExamesFacade getExamesFacade(){
		return this.examesFacade;
	}
	
	private IFaturamentoFacade getIFaturamentoFacade(){
		return this.faturamentoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelAgrpPesquisasDAO getAelAgrpPesquisasDAO(){
		return aelAgrpPesquisasDAO;
	}

}
