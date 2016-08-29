package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.AghWFTemplateEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.InformacaoAgendaVO;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.MarcaComercialMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.compras.dao.ScoMarcaComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoMaterialItem;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class OPMEPortalAgendamentoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(OPMEPortalAgendamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AghWFTemplateEtapaDAO aghWFTemplateEtapaDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@Inject
	private ScoMarcaComercialDAO scoMarcaComercialDAO;
	
	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4245278171014194456L;
	
	public enum OPMEPortalAgendamentoRNBusinessExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_REQ_FINALIZADA, MSG_ERRO_REQ_ANDAMENTO, MSG_ERRO_REQ_OBRIG, MSG_ERRO_REQ_SEM_JUST, MSG_ERRO_PROC_ESCALA 
	}

	private InformacaoAgendaVO obterInformacaoAgendaVO(MbcAgendas agenda, AghParametros convenioSusPadrao, AghParametros susPlanoInternacao) {
		InformacaoAgendaVO vo = new InformacaoAgendaVO();
		vo.setSeq(agenda.getSeq());
		vo.setDtAgenda(agenda.getDtAgenda());
		vo.setIndSituacao(agenda.getIndSituacao());
		vo.setPacCodigo(agenda.getPaciente().getCodigo());
		vo.setEprPciSeq(agenda.getProcedimentoCirurgico().getSeq());
		vo.setEprEspSeq(agenda.getEspProcCirgs().getId().getEspSeq());
		if (agenda.getFatConvenioSaude() != null) {
			vo.setCspCnvCodigo(agenda.getFatConvenioSaude().getCodigo());
		}
		if (agenda.getConvenioSaudePlano() != null) {
			vo.setCspSeq(agenda.getConvenioSaudePlano().getId().getSeq());
		}
		vo.setCnvSus(convenioSusPadrao.getVlrNumerico().shortValue());
		vo.setCspIntSeq(susPlanoInternacao.getVlrNumerico().byteValue());
		Date dtBase = DateUtil.adicionaDias(agenda.getDthrInclusao(), agenda.getUnidadeFuncional().getQtdDiasLimiteCirg().intValue());
		vo.setDtBase(dtBase);
		vo.setQtdDiasLimiteCirg(agenda.getUnidadeFuncional().getQtdDiasLimiteCirg().intValue());
		
		return vo;
	}
		
	// RN11_VERF_PRAZO
	public Integer verificarPrazoAgenda(MbcAgendas agenda) throws ApplicationBusinessException {
		MbcAgendas agd = null;
		if (agenda.getSeq() != null) {
			agd = getMbcAgendasDAO().consultarInformacoesAgenda(agenda.getSeq());
		} else {
			agd = agenda;
		}
		AghParametros convenioSusPadrao = getParametroFacade().getAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		AghParametros susPlanoInternacao = getParametroFacade().getAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		InformacaoAgendaVO vo = obterInformacaoAgendaVO(agd, convenioSusPadrao, susPlanoInternacao);
		
		if (DateUtil.validaDataMenor(vo.getDtAgenda(), vo.getDtBase())) {
			return vo.getQtdDiasLimiteCirg();
		}
		return 0;
	}
	
	public List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException {
		AghParametros prazoPlanCirg = getParametroFacade().getAghParametro(AghuParametrosEnum.P_AGHU_PRAZO_PLAN_CIRURGIA);
		return mbcAgendasDAO.buscaDadosProcessoAutorizacaoOpme(prazoPlanCirg.getVlrNumerico());
	}
	
	private void validarCompatibilidadeGrupoExcludencia(MbcOpmesVO opmesVO, List<MbcOpmesVO> listaPesquisada) {
		if (opmesVO.getCor() != null) {
			List<MbcOpmesVO> materiaisExcludencia = new ArrayList<MbcOpmesVO>();
			
			MbcOpmesVO maiorRegistro = getMaiorValorUnitario(opmesVO, listaPesquisada, materiaisExcludencia);
			
			//7.3.3. SIM:
			//	7.3.3.1. Se <QTD_SOLC> Maior que <QTD_AUTR_SUS> entÃ£o 
			//	<REG_CORRENTE>.<IND_COMPATIVEL> = â€˜Nâ€™; 
			//	<REG_CORRENTE>.<DESC_INCOMPAT> = C04_IPH_SUS.COD_TABELA + â€˜ â€“ â€™ + C04_IPH_SUS.DESCRICAO  + â€˜ (Quantidade maior que autorizada)â€™;
			if (maiorRegistro.getQtdeSol() > maiorRegistro.getQtdeAut()) {
				maiorRegistro.getItensRequisicaoOpmes().setIndCompativel(false);
				//5. <REQUISICAO_OPME>.<IND_AUTORIZADO> = <REQUISICAO_OPME>.<IND_COMPATIVEL>;
				maiorRegistro.getItensRequisicaoOpmes().setIndAutorizado(maiorRegistro.getItensRequisicaoOpmes().getIndCompativel());
				maiorRegistro.getItensRequisicaoOpmes().setDescricaoIncompativel(maiorRegistro.getItemProcedimentoHospitalar() + " (Quantidade maior que autorizada)");
			}
			
			//ApÃ³s identificado o registro de maior valor unitÃ¡rio dentro do Grupo, serÃ¡ avaliada as compatibilidades
			for (MbcOpmesVO vo : materiaisExcludencia) {
				if (vo.getCor() != null && vo.getCor().equals(opmesVO.getCor())) {
					if (vo.getQtdeSol() > 0) {
						if (!vo.getItemProcedimentoHospitalar().equals(maiorRegistro.getItemProcedimentoHospitalar()) || !vo.getItensRequisicaoOpmes().getMateriaisItemOpmes().equals(maiorRegistro.getItensRequisicaoOpmes().getMateriaisItemOpmes())) {
							//7.3.4. NÃƒO:
							//	7.3.4.1. Busca cÃ³digo e descriÃ§Ã£o do SUS atravÃ©s da consulta C04_IPH_SUS com os parÃ¢metros (<REG_MAX_VALOR>.<IPH_PHO_SEQ>, (<REG_MAX_VALOR>.<IPH_SEQ>)
							//	7.3.4.2. <REG_CORRENTE>.<IND_COMPATIVEL> = â€˜Nâ€™;
							//	7.3.4.3. <REG_CORRENTE>.<DESC_INCOMPAT> = 
							//	C04_IPH_SUS(<REG_CORRENTE>).COD_TABELA + â€˜ â€“ â€™ + C04_IPH_SUS(<REG_CORRENTE>).DESCRICAO
							//	+ â€˜ incompatÃ­vel com â€™ 
							//	C04_IPH_SUS(<REG_MAX_VALOR>).COD_TABELA + â€˜ â€“ â€™ + C04_IPH_SUS(<REG_MAX_VALOR>).DESCRICAO
							
							vo.getItensRequisicaoOpmes().setIndCompativel(false);
							//5. <REQUISICAO_OPME>.<IND_AUTORIZADO> = <REQUISICAO_OPME>.<IND_COMPATIVEL>;
							vo.getItensRequisicaoOpmes().setIndAutorizado(vo.getItensRequisicaoOpmes().getIndCompativel());
							StringBuffer sb = new StringBuffer();
							sb.append(vo.getItemProcedimentoHospitalar())
								.append(" incompatível com ")
								.append(maiorRegistro.getItemProcedimentoHospitalar());
							
							if (vo.getQtdeSol() > vo.getQtdeAut()) {
								sb.append(" (Mesmo Grupo e Quantidade maior que autorizada)");
							} else {
								sb.append(" (Mesmo Grupo)");
							}
							
							vo.getItensRequisicaoOpmes().setDescricaoIncompativel(sb.toString());
						}
					}
				}
			}
		}
	}
	
	public Double atualizaTotalCompativel(List<MbcOpmesVO> listaPesquisada) {
		BigDecimal totalCompativel = BigDecimal.ZERO;
		
		for (MbcOpmesVO mbcOpmesVO : listaPesquisada) {
			
			if(mbcOpmesVO.getVoQuebra() != null){
				continue;
			}
			
			if (mbcOpmesVO.getQtdeSol() > 0) { 
				if (mbcOpmesVO.getItensRequisicaoOpmes().getIndCompativel()) {
					totalCompativel = totalCompativel.add(mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeSol().intValue())));
				} else {
					if (mbcOpmesVO.getCor() != null)  {
						List<MbcOpmesVO> materiaisExcludencia = getVerificaMaior(listaPesquisada, mbcOpmesVO);
						MbcOpmesVO maiorValorUnitario = getVerificaMaiorUnitarioOpme(mbcOpmesVO, materiaisExcludencia);
						
						if (mbcOpmesVO.equals(maiorValorUnitario)) {
							totalCompativel = getCalculaCompativelAutorizado(totalCompativel, mbcOpmesVO);
						}
						
					} else {
						totalCompativel = getCalculaCompativelAutorizado(totalCompativel, mbcOpmesVO);
					}
					
				}
			}
			
		}
		return totalCompativel.doubleValue();
	}

	private BigDecimal getCalculaCompativelAutorizado(
			BigDecimal totalCompativel, MbcOpmesVO mbcOpmesVO) {
		if (mbcOpmesVO.getQtdeSol() > mbcOpmesVO.getQtdeAut()) {
			totalCompativel = totalCompativel.add(mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeAut().intValue())));
		} else {
			totalCompativel = totalCompativel.add(mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeSol().intValue())));
		}
		return totalCompativel;
	}
	
	public Double atualizarIncompatibilidades(MbcRequisicaoOpmes requisicaoOpmes, StringBuffer incompatibilidadeEncontradas) {
		BigDecimal totalIncompativel = BigDecimal.ZERO;
		
		String alfabeto = "abcdefghijlmnopqrstuvxz";
		Integer index = 0;
		String quebraLinha = "\n";
		if (requisicaoOpmes != null && requisicaoOpmes.getItensRequisicao() != null && !requisicaoOpmes.getItensRequisicao().isEmpty()) {
			for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) {
				if (!item.getIndCompativel()) {
					
					totalIncompativel = totalIncompativel.add((item.getValorUnitarioIph().multiply(new BigDecimal(item.getQuantidadeSolicitada() - item.getQuantidadeAutorizadaSus())))).abs();
					
					if (item.getDescricaoIncompativel() != null) {
						index = escreverDescricaoIncompativel(incompatibilidadeEncontradas, alfabeto, index, quebraLinha, item);
					}
				}
			}
		}
		return totalIncompativel.doubleValue();
	}
	
	public Double atualizarIncompatibilidades(MbcRequisicaoOpmes requisicaoOpmes, StringBuffer incompatibilidadeEncontradas, List<MbcOpmesVO> listaPesquisada) {
		BigDecimal totalIncompativel = BigDecimal.ZERO;
		String alfabeto = "abcdefghijlmnopqrstuvxz";
		Integer index = 0;
		String quebraLinha = "\n";
		
		for (MbcOpmesVO mbcOpmesVO : listaPesquisada) {
			
			if(mbcOpmesVO.getVoQuebra() != null){
				continue;
			}
			
			if (mbcOpmesVO.getQtdeSol() > 0 && !mbcOpmesVO.getItensRequisicaoOpmes().getIndCompativel()) {
				if (mbcOpmesVO.getCor() != null)  {
					List<MbcOpmesVO> materiaisExcludencia = getVerificaMaior(listaPesquisada, mbcOpmesVO);
					MbcOpmesVO maiorValorUnitario = getVerificaMaiorUnitarioOpme(mbcOpmesVO, materiaisExcludencia);
					
					if (mbcOpmesVO.equals(maiorValorUnitario)) {
						if (mbcOpmesVO.getQtdeSol() > mbcOpmesVO.getQtdeAut()) {
							totalIncompativel = totalIncompativel.add((mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeSol() - mbcOpmesVO.getQtdeAut())))).abs();
						}
					} else {
						totalIncompativel = totalIncompativel.add((mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeSol())))).abs();
					}
					
				} else {
					totalIncompativel = totalIncompativel.add((mbcOpmesVO.getValorUnit().multiply(new BigDecimal(mbcOpmesVO.getQtdeSol() - mbcOpmesVO.getQtdeAut())))).abs();
				}
			}
		}
		for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) {
			if (!item.getIndCompativel()) {
				if (item.getDescricaoIncompativel() != null) {
					index = escreverDescricaoIncompativel(incompatibilidadeEncontradas, alfabeto, index, quebraLinha, item);
				}
			}
		}
		
		return totalIncompativel.doubleValue();
	}

	private Integer escreverDescricaoIncompativel(
			StringBuffer incompatibilidadeEncontradas, String alfabeto,
			Integer index, String quebraLinha, MbcItensRequisicaoOpmes item) {
		if (index == alfabeto.length() - 1) {
			index = 0;
		}
		
		incompatibilidadeEncontradas.append(Character.toString(alfabeto.charAt(index))).append(") ");
		
		incompatibilidadeEncontradas.append(item.getDescricaoIncompativel()).append(quebraLinha);
		index++;
		return index;
	}
	
	public MbcItensRequisicaoOpmes criaPojosAdc(MbcRequisicaoOpmes requisicaoOpmes, MaterialOpmeVO materialOpme, String solicitacaoMaterial, Integer qtdeSolicitada) {
		ScoMaterial material = null;
		ScoMarcaComercial marca = null;
		if(materialOpme != null){
			material = scoMaterialDAO.obterPorChavePrimaria(materialOpme.getMatCodigo());
			marca	 = scoMarcaComercialDAO.obterPorChavePrimaria(materialOpme.getMatMarcaCod());
		}
		
		MbcItensRequisicaoOpmes item = new MbcItensRequisicaoOpmes();
		item.setRequisicaoOpmes(requisicaoOpmes);
		item.setMateriaisItemOpmes(new ArrayList<MbcMateriaisItemOpmes>());
		
		item.setIndCompativel(false);
		item.setIndAutorizado(false);
		item.setIndConsumido(false);
		
		item.setQuantidadeSolicitada(qtdeSolicitada);
		item.setQuantidadeAutorizadaSus((short)0);
		item.setQuantidadeAutorizadaHospital(0);
		item.setQuantidadeConsumida(0);
		item.setValorUnitarioIph(BigDecimal.ZERO);

		if (material != null) {
			item.setRequerido(DominioRequeridoItemRequisicao.ADC);
			MbcMateriaisItemOpmes materialItemOpmes = new MbcMateriaisItemOpmes();
			materialItemOpmes.setSituacao(DominioSituacaoMaterialItem.A);
			item.getMateriaisItemOpmes().add(materialItemOpmes);
			materialItemOpmes.setItensRequisicaoOpmes(item);
			materialItemOpmes.setMaterial(material);
			materialItemOpmes.setQuantidadeSolicitada(qtdeSolicitada.intValue());
			materialItemOpmes.setQuantidadeConsumida(0);
			item.setDescricaoIncompativel("Novo material adicionado: " +material.getCodigoENome());
			if(marca != null){
				materialItemOpmes.setScoMarcaComercial(marca);
			}
			if(materialOpme.getIphValorUnit() != null){
				item.setValorUnitarioIph(materialOpme.getIphValorUnit());
			}
			// setar valor ValorUnitarioIph que vem do VO
			// setar a Marca que vem do VO
		} else {
			item.setSolicitacaoNovoMaterial(solicitacaoMaterial);
			item.setDescricaoIncompativel("Novo material solicitado: " + item.getSolicitacaoNovoMaterial());
			item.setRequerido(DominioRequeridoItemRequisicao.NOV);
		}
		
		
		requisicaoOpmes.setIndCompativel(false);
		requisicaoOpmes.getItensRequisicao().add(item);
		return item;
	}
	public MbcOpmesVO adicionar(MbcItensRequisicaoOpmes item) {
		MbcMateriaisItemOpmes materiaisItemOpmes = null;
		if (item.getMateriaisItemOpmes() != null && !item.getMateriaisItemOpmes().isEmpty()) {
			materiaisItemOpmes = item.getMateriaisItemOpmes().iterator().next();
		}
		
		return adicionar(item, true, materiaisItemOpmes);
	}
	public MbcOpmesVO adicionar(MbcItensRequisicaoOpmes item, boolean excluir, MbcMateriaisItemOpmes materiaisItemOpmes) {
		Long iphCompCod = null;
		String iphCompDescr = null;
		if (item.getFatItensProcedHospitalar() != null) {
			iphCompCod = item.getFatItensProcedHospitalar().getCodTabela();
			iphCompDescr = item.getFatItensProcedHospitalar().getDescricao();
		}
		MbcOpmesVO vo = new MbcOpmesVO(item, excluir, iphCompCod, iphCompDescr, materiaisItemOpmes);
		
		calculaQuantidadeAutorizada(vo);
		vo.setCodTabela(iphCompCod);
		
		if (vo.getMbcMateriaisItemOpmes() != null) {
			if (vo.getMbcMateriaisItemOpmes().getScoMarcaComercial() != null) {
				vo.setCodigoMarcasComerciais(vo.getMbcMateriaisItemOpmes().getScoMarcaComercial()
						.getCodigo());
				vo.setDescricaoMarcasComerciais(vo.getMbcMateriaisItemOpmes()
						.getScoMarcaComercial().getDescricao());
			}
			if (vo.getMbcMateriaisItemOpmes().getMaterial() != null) {
				vo.setUnidadeMaterial(vo.getMbcMateriaisItemOpmes().getMaterial().getUmdCodigo());
			}
		}
		
		return vo;
	}
	
	/**
		5. Se <ITENS_REQUISICAO_OPMES>.<QTD_SOLC> Maior que <ITENS_REQUISICAO_OPMES>.<QTD_AUT_SUS> Então:
		5.1. <ITENS_REQUISICAO_OPMES>.<IND_COMPATIVEL> = ‘N’
		5.2. <ITENS_REQUISICAO_OPMES>.<DESC_INCOMPAT> = ‘Quantidade maior que autorizada’
		6. Senão
		6.1. <ITENS_REQUISICAO_OPMES>.<IND_COMPATIVEL> = ‘S’
		6.2. <ITENS_REQUISICAO_OPMES>.<DESC_INCOMPAT> = ‘’
	*/
	public void calculaQuantidadeAutorizada(MbcOpmesVO opmeVO) {
		MbcOpmesVO voRef = opmeVO.getVoQuebra() == null ? opmeVO : opmeVO.getVoQuebra();
		
		calculaTotalSolicitado(voRef);

		MbcItensRequisicaoOpmes item = voRef.getItensRequisicaoOpmes();

		if (item.getQuantidadeSolicitada() > item.getQuantidadeAutorizadaSus()) {
			if (!DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido()) &&
					!DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())) {
				item.setIndCompativel(false);
				item.setDescricaoIncompativel(voRef.getItemProcedimentoHospitalar() + " (Quantidade maior que autorizada)");
			}
			
			voRef.setCorQtdeSol("color: red;");
			
		} else {
			item.setIndCompativel(true);
			item.setDescricaoIncompativel("");
			voRef.setCorQtdeSol("color: black;");
		}
		
		item.setIndAutorizado(item.getIndCompativel());
	}


	public void calculaQuantidades(List<MbcOpmesVO> listaPesquisada) {
		for (MbcOpmesVO mbcOpmesVO : listaPesquisada) {
			if (mbcOpmesVO.getVoQuebra() ==  null) {
				calculaTotalSolicitado(mbcOpmesVO);
			}
		}
	}
	
	public void calculaTotalSolicitado(MbcOpmesVO voQuebra) {
		voQuebra.setQtdeSol(0);
		if (voQuebra.getFilhos() != null && !voQuebra.getFilhos().isEmpty()) {
			for (MbcOpmesVO vo : voQuebra.getFilhos()) {
				voQuebra.setQtdeSol((voQuebra.getQtdeSol() + vo.getQtdeSolicitadaMaterial()));
			}
		} else {
			voQuebra.setQtdeSol(voQuebra.getQtdeSolicitadaMaterial());
		}
		MbcItensRequisicaoOpmes item = voQuebra.getItensRequisicaoOpmes();
		item.setQuantidadeSolicitada(voQuebra.getQtdeSol());
	}
	
	// RN03_CMPOS_REQ
	public Boolean validarVisualizacaoCamposRequisicao(MbcRequisicaoOpmes requisicao) {
		Boolean retorno = Boolean.TRUE;
		
		if (!DominioSituacaoRequisicao.INCOMPATIVEL.equals(requisicao.getSituacao())
			&& !DominioSituacaoRequisicao.COMPATIVEL.equals(requisicao.getSituacao())
			&& !requisicao.getIndCompativel()) {
			retorno = Boolean.FALSE;
		}
		
		return retorno;
	}
	
	// RN03_CMPOS_REQ
	public Boolean validarHabilitacaoCamposRequisicao(MbcRequisicaoOpmes requisicao) {
		Boolean retorno = Boolean.TRUE;
		
		if (!DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicao.getSituacao())
			&& !requisicao.getIndCompativel()) {
			retorno = Boolean.FALSE;
		}
		
		return retorno;
	} 

	
	public List<MbcOpmesVO> carregaGrid(MbcRequisicaoOpmes requisicaoOpmes) {
		List<MbcOpmesVO> opmesVOs = new ArrayList<MbcOpmesVO>();
		if (requisicaoOpmes != null) {
			
			List<MbcItensRequisicaoOpmes> itensOrdenadosNOV = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenadosADC = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenadosREQNRQ = new ArrayList<MbcItensRequisicaoOpmes>();
			List<MbcItensRequisicaoOpmes> itensOrdenados = new ArrayList<MbcItensRequisicaoOpmes>();
			
			for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) {
				if (DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido())) {
					itensOrdenadosNOV.add(item);
				}
				if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())) {
					itensOrdenadosADC.add(item);
				}
				if (DominioRequeridoItemRequisicao.NRQ.equals(item.getRequerido()) || DominioRequeridoItemRequisicao.REQ.equals(item.getRequerido())) {
					itensOrdenadosREQNRQ.add(item);
				}
			}
			
			itensOrdenados.addAll(itensOrdenadosNOV);
			itensOrdenados.addAll(itensOrdenadosADC);
			itensOrdenados.addAll(itensOrdenadosREQNRQ);
			
			for (MbcItensRequisicaoOpmes item : itensOrdenados) {
				MbcMateriaisItemOpmes mbcMateriaisItemOpmes = null;
				if (item.getMateriaisItemOpmes() != null && !item.getMateriaisItemOpmes().isEmpty()) {
					mbcMateriaisItemOpmes = item.getMateriaisItemOpmes().iterator().next();
				}
				MbcOpmesVO vo = adicionar(item, true, mbcMateriaisItemOpmes);
				vo.getFilhos().add(vo);
				opmesVOs.add(vo);
				int index = 0;
				for (MbcMateriaisItemOpmes materialItemOpmes : item.getMateriaisItemOpmes()) {
					materialItemOpmes = mbcMateriaisItemOpmesDAO.obterPorChavePrimaria(materialItemOpmes.getSeq());
					if (index > 0) {
						MbcOpmesVO voMaterial = adicionar(item, true, materialItemOpmes);
						List<MarcaComercialMaterialVO> marcas = mbcItensRequisicaoOpmesDAO.recuperaMarcasMaterial(materialItemOpmes.getMaterial().getCodigo(),requisicaoOpmes.getAgendas().getDtAgenda());
						if(marcas != null && marcas.size()>0){
							voMaterial.setValorUnitario(marcas.get(0).getValorUnitario());
						}
						voMaterial.setVoQuebra(vo);
						if(!opmesVOs.contains(voMaterial)){
							opmesVOs.add(voMaterial);
						}
						if(!vo.getFilhos().contains(voMaterial)){
							vo.getFilhos().add(voMaterial);
						}
						
					}
					index++;
				}
			}
		}
		
		opmesVOs = calculaQuantidade(opmesVOs);

		
		return opmesVOs;

	}
	
	private List<MbcOpmesVO> calculaQuantidade(List<MbcOpmesVO> opmesVOs){
		for (MbcOpmesVO mbcOpmesVO : opmesVOs) {
			calculaQuantidadeAutorizada(mbcOpmesVO);
		}
		return opmesVOs;
	}
	
	// #33880 - chamada para RN01
	public MbcItensRequisicaoOpmes validarMaterialExcludenciaEQuantidade(MbcOpmesVO vo, List<MbcOpmesVO> listaVo, FatItensProcedHospitalar procedimentoSUS, List<GrupoExcludenteVO> listaGrupoExcludente) {
		// PASSO 02
		Boolean pertenceGrupo = validarItemRequisicaoMaterialGrupoExcludencia(procedimentoSUS, vo);
		MbcItensRequisicaoOpmes itemRequisicao = vo.getItensRequisicaoOpmes();
		
		if (!pertenceGrupo) {
			getValidacaoQuantidadeSolicitadaEAutorizadaItemMaterial(vo);
		} else {
			consultarMateriaisMutalmenteExclusivos(vo, listaGrupoExcludente, listaVo);
		}
		
		return itemRequisicao;
	}
	
	// #33880 - PASSO 02
	private Boolean validarItemRequisicaoMaterialGrupoExcludencia(FatItensProcedHospitalar procedimentoSUS, MbcOpmesVO vo) {
		
		FatCompatExclusItem item = getMbcRequisicaoOpmesDAO().consultarRelacionamentoProcedimentoSUSMaterialOpme(procedimentoSUS.getId().getPhoSeq(), procedimentoSUS.getId().getSeq(), 
																					  vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq(), vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq());
		
		if (!DominioIndComparacao.R.equals(item.getIndComparacao()) && !DominioIndCompatExclus.ICP.equals(item.getIndCompatExclus())) {
			vo.getItensRequisicaoOpmes().setIndCompativel(true);
			StringBuffer descricao = new StringBuffer(100);
			descricao.append(vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getCodTabela())
			.append('-')
			.append(vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getDescricao())
			.append("(Material cadastrado como: ")
			.append(item.getIndComparacao().getDescricao())
			.append(' ')
			.append(item.getIndCompatExclus().getDescricao())
			.append(". Sua compatibilidade não será verificada)");
			vo.getItensRequisicaoOpmes().setDescricaoIncompativel(descricao.toString());
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	private void validarQuantidadeSolicitadaEAutorizadaItemMaterial(MbcItensRequisicaoOpmes itemCorrente) {
		if (itemCorrente.getQuantidadeSolicitada() > itemCorrente.getQuantidadeAutorizadaSus()) {
			itemCorrente.setIndCompativel(false);
			String descricao = itemCorrente.getFatItensProcedHospitalar().getCodTabela() + "-" + itemCorrente.getFatItensProcedHospitalar().getDescricao() + " (Quantidade maior que autorizada)";
			itemCorrente.setDescricaoIncompativel(descricao);
		}
	}
	
	private void getValidacaoQuantidadeSolicitadaEAutorizadaItemMaterial(MbcOpmesVO vo) {
		if (vo.getItensRequisicaoOpmes().getQuantidadeSolicitada() > vo.getItensRequisicaoOpmes().getQuantidadeAutorizadaSus()) {
			vo.getItensRequisicaoOpmes().setIndCompativel(false);
			String descricao = vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getCodTabela() + "-" + vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getDescricao() + " (Quantidade maior que autorizada) ";
			vo.getItensRequisicaoOpmes().setDescricaoIncompativel(descricao);
		}
	}
	
	// #33880 - PASSO 06
	private void consultarMateriaisMutalmenteExclusivos(MbcOpmesVO vo, List<GrupoExcludenteVO> listaGrupoExcludente, List<MbcOpmesVO> listaVo) {
		/**
		 * 3. Identifica o Grupo de Excludências: Consultar [RecordSet Grupos Excludentes], passando como parâmetro:
			Material_Principal_PHO = <ITENS_REQUISICAO_OPMES>.<IPH_PHO_SEQ>
			Material_Principal_IPH = <ITENS_REQUISICAO_OPMES>.<IPH_SEQ>
		 */
		
		GrupoExcludenteVO grupoExc = null;
		if (listaGrupoExcludente != null) {
			for (GrupoExcludenteVO grupoExcludente : listaGrupoExcludente) {
				if (grupoExcludente.getIphPhoSeq().equals(vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq()) 
					&& grupoExcludente.getIphSeq().equals(vo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq())) {
					grupoExc = grupoExcludente;
					break;
				}
			}
		}
		/**
		 * 4. Se NÃO possuir Grupo (não encontrar registro no [RecordSet Grupos Excludentes]):
			4.1. Se <QTD_SOLC> Maior que <QTD_AUTR_SUS> Então
			<IND_COMPATIVEL> = ‘N’
			<DESC_INCOMPAT> = C04_IPH_SUS.COD_TABELA + ‘ – ’ + C04_IPH_SUS.DESCRICAO + ‘ (Quantidade maior que autorizada)’
			4.2. Vai para último passo;
		 */
		if (grupoExc == null) {
			getValidacaoQuantidadeSolicitadaEAutorizadaItemMaterial(vo);
		} else {
			MbcItensRequisicaoOpmes itemRequisicaoMV = identificarItemRequisicaoMaiorValor(vo, listaVo, grupoExc);
			validarCompatibilidadeMaterial(itemRequisicaoMV, listaVo, grupoExc);
		}
		
	}

	private MbcItensRequisicaoOpmes identificarItemRequisicaoMaiorValor(MbcOpmesVO vo2, List<MbcOpmesVO> listaVo, GrupoExcludenteVO grupoExc) {
		/**
		 * PASSO 05 e 06
		 */
		MbcItensRequisicaoOpmes requisicao = vo2.getItensRequisicaoOpmes();
		
		for (ItemProcedimentoVO vo : grupoExc.getListaGrupoExcludente()) {
			MbcOpmesVO itemListagemVO = obterItemListagemMbcOpmesVO(listaVo, vo);
			if (itemListagemVO != null) {
				MbcItensRequisicaoOpmes itemCorrente = itemListagemVO.getItensRequisicaoOpmes();
				if (itemCorrente != null && itemCorrente.getQuantidadeSolicitada() > 0) {
					if ((itemCorrente.getQuantidadeSolicitada() * itemCorrente.getValorUnitarioIph().shortValue()) > (requisicao.getQuantidadeSolicitada() * requisicao.getValorUnitarioIph().shortValue())) {
						requisicao = itemCorrente;
					}
				}
			}
		}
		
		return requisicao;
	}

	private MbcItensRequisicaoOpmes validarCompatibilidadeMaterial(MbcItensRequisicaoOpmes itemRequisicao, List<MbcOpmesVO> listaVo, GrupoExcludenteVO grupoExc) {
		for (ItemProcedimentoVO vo : grupoExc.getListaGrupoExcludente()) {
			MbcOpmesVO itemListagemVO = obterItemListagemMbcOpmesVO(listaVo, vo);
			if (itemListagemVO != null) {
				this.gerarDescricaoIncompatibilidadesEncontradas(itemListagemVO, itemRequisicao);
			}
		}
		return itemRequisicao;
	}
	
	public MbcItensRequisicaoOpmes validarItensRequisicao(MbcItensRequisicaoOpmes itemCorrente, MbcItensRequisicaoOpmes itemRequisicao) {
		
		itemCorrente.setIndCompativel(false);
		itemCorrente.setDescricaoIncompativel(null);
		
		if (itemCorrente.getQuantidadeSolicitada() > 0) {
			if (itemCorrente.getFatItensProcedHospitalar() != null && itemRequisicao.getFatItensProcedHospitalar() != null && 
					itemCorrente.getFatItensProcedHospitalar().getId().getPhoSeq().equals(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq())
				&& itemCorrente.getFatItensProcedHospitalar().getId().getSeq().equals(itemRequisicao.getFatItensProcedHospitalar().getId().getSeq())) {
				
				validarQuantidadeSolicitadaEAutorizadaItemMaterial(itemCorrente);
			
			} else {
				
				itemCorrente.setIndCompativel(false);
				
				StringBuffer descricao = new StringBuffer();
				if (itemCorrente.getFatItensProcedHospitalar() != null && itemRequisicao.getFatItensProcedHospitalar() != null){
					descricao.append(itemCorrente.getFatItensProcedHospitalar().getCodTabela() + "-" + itemCorrente.getFatItensProcedHospitalar().getDescricao() + " incompatível com " + itemRequisicao.getFatItensProcedHospitalar().getCodTabela() + "-" + itemRequisicao.getFatItensProcedHospitalar().getDescricao());
				}
				
				if (itemCorrente.getQuantidadeSolicitada() > itemCorrente.getQuantidadeAutorizadaSus()) {
					descricao.append("(Mesmo Grupo e Quantidade maior que autorizada)");
					itemCorrente.setDescricaoIncompativel(descricao.toString());
				} else {
					descricao.append("(Mesmo Grupo)");
					itemCorrente.setDescricaoIncompativel(descricao.toString());
				}
			}
		}
		
		return itemRequisicao;
		
	}
	
	public MbcItensRequisicaoOpmes gerarDescricaoIncompatibilidadesEncontradas(MbcOpmesVO itemListagemVO, MbcItensRequisicaoOpmes itemRequisicao) {
		
		itemListagemVO.getItensRequisicaoOpmes().setIndCompativel(false);
		itemListagemVO.getItensRequisicaoOpmes().setDescricaoIncompativel(null);
		
		if (itemListagemVO.getItensRequisicaoOpmes().getQuantidadeSolicitada() > 0) {
			if (itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar() != null && itemRequisicao.getFatItensProcedHospitalar() != null && 
					itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq().equals(itemRequisicao.getFatItensProcedHospitalar().getId().getPhoSeq())
				&& itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq().equals(itemRequisicao.getFatItensProcedHospitalar().getId().getSeq())) {
				
				getValidacaoQuantidadeSolicitadaEAutorizadaItemMaterial(itemListagemVO);
			
			} else {
				
				itemListagemVO.getItensRequisicaoOpmes().setIndCompativel(false);
				
				StringBuffer descricao = new StringBuffer();
				if (itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar() != null && itemRequisicao.getFatItensProcedHospitalar() != null){
					descricao.append(itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getCodTabela() + "-" + itemListagemVO.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getDescricao() + " incompatível com " + itemRequisicao.getFatItensProcedHospitalar().getCodTabela() + "-" + itemRequisicao.getFatItensProcedHospitalar().getDescricao());
				}
				
				if (itemListagemVO.getItensRequisicaoOpmes().getQuantidadeSolicitada() > itemListagemVO.getItensRequisicaoOpmes().getQuantidadeAutorizadaSus()) {
					descricao.append("(Mesmo Grupo e Quantidade maior que autorizada)");
					itemListagemVO.getItensRequisicaoOpmes().setDescricaoIncompativel(descricao.toString());
				} else {
					descricao.append("(Mesmo Grupo)");
					itemListagemVO.getItensRequisicaoOpmes().setDescricaoIncompativel(descricao.toString());
				}
			}
		}
		
		return itemRequisicao;
		
	}

	private MbcOpmesVO obterItemListagemMbcOpmesVO(List<MbcOpmesVO> listaVo, ItemProcedimentoVO vo) {
		MbcOpmesVO itemListagemVO = null;
		for (MbcOpmesVO opmeVo : listaVo) {
			if (opmeVo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getPhoSeq().equals(vo.getIpxPhoSeq())
				&& opmeVo.getItensRequisicaoOpmes().getFatItensProcedHospitalar().getId().getSeq().equals(vo.getIpxSeq())) {
				itemListagemVO = opmeVo;
			}
		}
		return itemListagemVO;
	}
	
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	private MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO(){
		return mbcRequisicaoOpmesDAO;
	}
	
	
	private AghWFTemplateEtapaDAO getAghWFTemplateEtapaDAO(){
		return aghWFTemplateEtapaDAO;
	}
	

	private MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	private IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	public void solicitaMaterial(MbcOpmesVO vo, int quantidade, List<MbcOpmesVO> listaPesquisada, FatItensProcedHospitalar procedimentoSus, List<GrupoExcludenteVO> listaGrupoExcludenteVO) {
		if (quantidade != 0) {
			vo.setQtdeSolicitadaMaterial(vo.getQtdeSolicitadaMaterial() + quantidade);
		}
		
		if (vo.getQtdeSolicitadaMaterial() >= 0) {
			if (vo.getMbcMateriaisItemOpmes() != null) {
				vo.getMbcMateriaisItemOpmes().setQuantidadeSolicitada(vo.getQtdeSolicitadaMaterial());
			} else {
				vo.getItensRequisicaoOpmes().setQuantidadeSolicitada(vo.getQtdeSolicitadaMaterial());
			}
			if (DominioRequeridoItemRequisicao.NRQ.equals(vo.getItensRequisicaoOpmes().getRequerido()) || DominioRequeridoItemRequisicao.REQ.equals(vo.getItensRequisicaoOpmes().getRequerido())) {
				MbcOpmesVO voAlterar = vo;
				
				if (vo.getVoQuebra() != null) {
					voAlterar = vo.getVoQuebra();
				}
				
				if (vo.getQtdeSolicitadaMaterial() > 0) {
					voAlterar.getItensRequisicaoOpmes().setRequerido(DominioRequeridoItemRequisicao.REQ);
				} else if (vo.getQtdeSolicitadaMaterial() == 0) {
					voAlterar.getItensRequisicaoOpmes().setRequerido(DominioRequeridoItemRequisicao.NRQ);
				}
			}
			
			calculaQuantidadeAutorizada(vo);
		} else {
			vo.setQtdeSolicitadaMaterial(vo.getQtdeSolicitadaMaterialOld());
		}
		
		validarCompatibilidadeGrupoExcludencia(vo, listaPesquisada);
	}

	public MbcRequisicaoOpmes getCopiaRequisicao(MbcRequisicaoOpmes requisicaoOpmes) throws IllegalAccessException, InvocationTargetException {
		MbcRequisicaoOpmes old = new MbcRequisicaoOpmes();
		
		ConvertUtils.register(new DateConverter(null), Date.class);
		ConvertUtils.register(new IntegerConverter(null), Integer.class);
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		
		if (requisicaoOpmes != null) {
			BeanUtils.copyProperties(old, requisicaoOpmes);
			old.setItensRequisicao(new ArrayList<MbcItensRequisicaoOpmes>());
			
			for (MbcItensRequisicaoOpmes item : requisicaoOpmes.getItensRequisicao()) {
				MbcItensRequisicaoOpmes itemOld = new MbcItensRequisicaoOpmes();
				BeanUtils.copyProperties(itemOld, item);
				old.getItensRequisicao().add(itemOld);
	
				itemOld.setMateriaisItemOpmes(new ArrayList<MbcMateriaisItemOpmes>());
				for (MbcMateriaisItemOpmes material : item.getMateriaisItemOpmes()) {
					MbcMateriaisItemOpmes materialOld = new MbcMateriaisItemOpmes();
					BeanUtils.copyProperties(materialOld, material);
					itemOld.getMateriaisItemOpmes().add(materialOld);
				}
			}
		}
		return old;
	}

	public void validaJustificativa(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException {
		if (requisicaoOpmes.getJustificativaRequisicaoOpme() ==  null || requisicaoOpmes.getJustificativaRequisicaoOpme().isEmpty()) {
			throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_REQ_SEM_JUST);
		}
	}

	public Boolean isPermiteAlteracaoRequisicao(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null){
			if (DominioSituacaoRequisicao.COMPATIVEL.equals(requisicaoOpmes.getSituacao()) ||
					DominioSituacaoRequisicao.INCOMPATIVEL.equals(requisicaoOpmes.getSituacao()) ||
					DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicaoOpmes.getSituacao())||
					DominioSituacaoRequisicao.CANCELADA.equals(requisicaoOpmes.getSituacao())) {
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}
		
	}

	public String getEtapaProcessoAutorizacao(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null){
			AghWFTemplateEtapa etapa = getAghWFTemplateEtapaDAO().consultarEtapaWorkflowRequisicao(requisicaoOpmes);
			return etapa != null ? etapa.getDescricao() : "";
		}else{
			return null;
		}
	}
	
	public void validaRequisicaoEscala(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException{
		if(requisicaoOpmes.getSeq() != null){
			requisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicaoOpmes.getSeq());
			if(requisicaoOpmes != null && requisicaoOpmes.getAgendas() != null && requisicaoOpmes.getAgendas().getIndSituacao().equals(DominioSituacaoAgendas.ES)){
				throw new ApplicationBusinessException(OPMEPortalAgendamentoRNBusinessExceptionCode.MSG_ERRO_PROC_ESCALA);									   
			}
		}
	}
	
	public Boolean isRequisicaoEscalada(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null){
			if (requisicaoOpmes != null && requisicaoOpmes.getSeq() != null) {
				requisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicaoOpmes.getSeq());
			}
			
			if (requisicaoOpmes != null && requisicaoOpmes.getAgendas() != null && requisicaoOpmes.getAgendas().getIndSituacao().equals(DominioSituacaoAgendas.ES)) {
				return true; 
			}
		}
		return false;
	}
	
	public Boolean isRequisicaoOpmeIncompativel(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null){
			if (requisicaoOpmes != null && requisicaoOpmes.getSeq() != null) {
				requisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicaoOpmes.getSeq());
			}
			if(requisicaoOpmes != null && !requisicaoOpmes.getIndCompativel()){
				return true;
			}
		}
		return false;
	}
	
	public Boolean isRequisicaoOpmeIncompativelSemEscala(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null){
			if (requisicaoOpmes.getSeq() != null) {
				requisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicaoOpmes.getSeq());
			}
			if(!requisicaoOpmes.getIndCompativel() &&
			   requisicaoOpmes.getAgendas() != null && !requisicaoOpmes.getAgendas().getIndSituacao().equals(DominioSituacaoAgendas.ES) &&
			   DominioSituacaoRequisicao.COMPATIVEL.equals(requisicaoOpmes.getSituacao()) ||
			   DominioSituacaoRequisicao.INCOMPATIVEL.equals(requisicaoOpmes.getSituacao()) ||
			   DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(requisicaoOpmes.getSituacao())   ){
				return false;
			}
		}
		
		return true;
	}
	
	public Boolean isRequisicaoFinalizada(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null && 
		   DominioSituacaoRequisicao.AUTORIZADA.equals(requisicaoOpmes.getSituacao()) ||
		   DominioSituacaoRequisicao.CONCLUIDA.equals(requisicaoOpmes.getSituacao()) ||
		   DominioSituacaoRequisicao.CANCELADA.equals(requisicaoOpmes.getSituacao())){
			return true;
		}
		return false;
	}
	
	public Boolean isRequisicaoAndamento(MbcRequisicaoOpmes requisicaoOpmes) {
		if(requisicaoOpmes != null && 
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.INCOMPATIVEL) &&
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.COMPATIVEL) &&
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.AUTORIZADA) &&
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.NAO_AUTORIZADA) &&
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.CONCLUIDA) &&
		  !requisicaoOpmes.getSituacao().equals(DominioSituacaoRequisicao.CANCELADA)){
			return true;
		}
		return false;
	}
	
	private MbcOpmesVO getMaiorValorUnitario(MbcOpmesVO opmesVO, List<MbcOpmesVO> listaPesquisada, List<MbcOpmesVO> materiaisExcludencia) {
		MbcOpmesVO maiorRegistro = opmesVO;
		//Registro editado na tela está em um Grupo de Excludências
		//Busca o registro selecionado, dentro do GRUPO, de maior Valor Unitário (MAX (<Val.Unit.>)):
		//<REG_MAX_VALOR>:  POJO identificado com o maior “valor unitário” dentro do grupo de excludência
		for (MbcOpmesVO vo : listaPesquisada) {
			if (vo.getCor() != null && vo.getCor().equals(opmesVO.getCor())) {
				if (vo.getQtdeSol() > 0) {
					if (maiorRegistro == null || vo.getValorUnit().compareTo(maiorRegistro.getValorUnit()) == 1 || maiorRegistro.getQtdeSol() == 0) {
						maiorRegistro = vo;
					}
				}
				materiaisExcludencia.add(vo);
				vo.getItensRequisicaoOpmes().setIndCompativel(true);
				vo.getItensRequisicaoOpmes().setIndAutorizado(vo.getItensRequisicaoOpmes().getIndCompativel());
				vo.getItensRequisicaoOpmes().setDescricaoIncompativel("");
			}
		}
		return maiorRegistro;
	}
	
	public void setCompatibilidadeGrupoExcludencia(List<MbcOpmesVO> listaPesquisada) {
		for (MbcOpmesVO vo : listaPesquisada) {
			validarCompatibilidadeGrupoExcludencia(vo, listaPesquisada);
		}
	}
	
	private List<MbcOpmesVO> getVerificaMaior(List<MbcOpmesVO> listaPesquisada,
			MbcOpmesVO mbcOpmesVO) {
		//verifica todos os materias do mesmo grupo que foram selecionados
		List<MbcOpmesVO> materiaisExcludencia = new ArrayList<MbcOpmesVO>();	
		for (MbcOpmesVO mesmaCor : listaPesquisada) {
			if (mesmaCor.getCor() != null && mesmaCor.getQtdeSol() > 0) {
				if (mesmaCor.getCor().equals(mbcOpmesVO.getCor())) {
					materiaisExcludencia.add(mesmaCor);
				}
			}
		}
		return materiaisExcludencia;
	}
	
	private MbcOpmesVO getVerificaMaiorUnitarioOpme(MbcOpmesVO mbcOpmesVO,
			List<MbcOpmesVO> materiaisExcludencia) {
		MbcOpmesVO maiorValorUnitario = mbcOpmesVO;
		for (MbcOpmesVO maiorValorUnitarioCorrente : materiaisExcludencia) {
			if (maiorValorUnitarioCorrente.getValorUnit().compareTo(maiorValorUnitario.getValorUnit()) == 1) {
				maiorValorUnitario = maiorValorUnitarioCorrente;
			}
		}
		return maiorValorUnitario;
	}

}
