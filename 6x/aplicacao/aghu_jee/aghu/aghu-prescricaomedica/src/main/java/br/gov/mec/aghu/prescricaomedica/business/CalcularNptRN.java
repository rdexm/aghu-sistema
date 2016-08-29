package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioTipoVolume;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.prescricaomedica.business.CalcularNptON.CalcularNptONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteComposicaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CalcularNptRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8240566145059251971L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private CalcularNptRN calcularNptRN;
	
	@EJB
	private PrescricaoNptRN prescricaoNptRN;

	@EJB
	private ItemPrescricaoNptRN itemPrescricaoNptRN;
	
	@EJB
	private ComposicaoPrescricaoNptRN composicaoPrescricaoNptRN;
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO composicaoPrescricaoNptDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;
	
	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;
	
	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	private static final Log LOG = LogFactory.getLog(CalcularNptRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	protected enum CalcularNptRNExceptionCode implements BusinessExceptionCode {
		MPM_03412;
	}
	
	public void mpmpIncluiCompA(MpmPrescricaoNptVO prescricaoNptVO, DominioIdentificacaoComponenteNPT identifComponente,
			CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal qtdeCalculada,
			BigDecimal qtdePrescrita, BigDecimal qtdeBaseCalculo, DominioTipoVolume tpParamCalcUsado, Integer ummSeq,
			BigDecimal totParam, Integer pcnSeqp, BigDecimal paramPerc) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		
		ComponenteComposicaoVO componenteComposicao = this.afaComponenteNptDAO.obterComponenteComposicao(identifComponente);
		
		if (componenteComposicao == null) {
			throw new ApplicationBusinessException(CalcularNptRNExceptionCode.MPM_03412);
		}
		
		List<MpmComposicaoPrescricaoNptVO> novasComposicoes = new ArrayList<MpmComposicaoPrescricaoNptVO>();
		
		for (MpmComposicaoPrescricaoNptVO cpt : prescricaoNptVO.getComposicoes()) {
			if (cpt.getTicSeq().equals(componenteComposicao.getTicSeq())) {
				// Insere componente
				MpmItemPrescricaoNptVO componente = inserirComponente(componenteComposicao, cpt, qtdeCalculada, qtdePrescrita, qtdeBaseCalculo, 
						tpParamCalcUsado, ummSeq, totParam, pcnSeqp, paramPerc);
				cpt.getComponentes().add(componente);
				
			} else {
				// Insere Composição
				MpmComposicaoPrescricaoNpt comp = new MpmComposicaoPrescricaoNpt();
				Short seqp = composicaoPrescricaoNptDAO.obterUltimoSeqpComposicao(prescricaoNptVO.getPnpSeq(), prescricaoNptVO.getPnpAtdSeq());
				comp.setId(new MpmComposicaoPrescricaoNptId(prescricaoNptVO.getPnpAtdSeq(), prescricaoNptVO.getPnpSeq(), (short) (seqp + Short.valueOf("1"))));
				comp.setAfaTipoComposicoes(afaTipoComposicoesDAO.obterPorChavePrimaria(componenteComposicao.getTicSeq()));
				if (componenteComposicao.getTicSeq().equals(calculoAdultoNptVO.getCalculoParametrosFixosVO().getComposicaoSolNpt().shortValue())) {
					comp.setVelocidadeAdministracao(calculoAdultoNptVO.getGotejoSolucao());
					
				} else {
					comp.setVelocidadeAdministracao(calculoAdultoNptVO.getGotejoLipidios());
				}
				this.composicaoPrescricaoNptRN.inserir(comp);
				MpmComposicaoPrescricaoNptVO composicao = this.parseVOComposicao(comp);
				// Insere componente
				MpmItemPrescricaoNptVO componente = inserirComponente(componenteComposicao, composicao, qtdeCalculada, 
						qtdePrescrita, qtdeBaseCalculo, tpParamCalcUsado, ummSeq, totParam, pcnSeqp, paramPerc);
				composicao.getComponentes().add(componente);
				// Adiciona a composição em uma lista temporária.
				novasComposicoes.add(composicao);
			}
		}
		prescricaoNptVO.getComposicoes().addAll(novasComposicoes);
	}

	private MpmItemPrescricaoNptVO inserirComponente(ComponenteComposicaoVO componenteComposicao, MpmComposicaoPrescricaoNptVO cpt,
			BigDecimal qtdeCalculada, BigDecimal qtdePrescrita, BigDecimal qtdeBaseCalculo, DominioTipoVolume tpParamCalcUsado, Integer ummSeq,
			BigDecimal totParam, Integer pcnSeqp, BigDecimal paramPerc) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		
		MpmItemPrescricaoNpt ipn = new MpmItemPrescricaoNpt();
		Short seqp = mpmItemPrescricaoNptDAO.obterUltimoSeqpComponente(cpt.getPnpAtdSeq(), cpt.getPnpSeq(), cpt.getSeqp());
		ipn.setId(new MpmItemPrescricaoNptId(cpt.getPnpAtdSeq(), cpt.getPnpSeq(), cpt.getSeqp(), (short) (seqp + Short.valueOf("1"))));
		ipn.setAfaComponenteNpts(afaComponenteNptDAO.obterPorChavePrimaria(componenteComposicao.getMedMatCodigo()));
		ipn.setAfaFormaDosagens(afaFormaDosagemDAO.obterPorChavePrimaria(componenteComposicao.getSeqDosagem()));
		this.moverComponente(ipn, qtdeCalculada, qtdePrescrita, qtdeBaseCalculo, tpParamCalcUsado, ummSeq, totParam, pcnSeqp, paramPerc);
		this.itemPrescricaoNptRN.inserir(ipn);
		MpmItemPrescricaoNptVO composicao = this.parseVOComponente(ipn);
		return composicao;
	}
	
	protected void moverComponente(MpmItemPrescricaoNpt mpmItemPrescricaoNpt, BigDecimal qtdeCalculada,
			BigDecimal qtdePrescrita, BigDecimal qtdeBaseCalculo, DominioTipoVolume tpParamCalcUsado, Integer ummSeq,
			BigDecimal totParam, Integer pcnSeqp, BigDecimal paramPerc) {		
		mpmItemPrescricaoNpt.setQtdeCalculada(qtdeCalculada);
		mpmItemPrescricaoNpt.setQtdePrescrita(qtdePrescrita);
		mpmItemPrescricaoNpt.setQtdeBaseCalculo(qtdeBaseCalculo);
		mpmItemPrescricaoNpt.setTipoParamCalculo(tpParamCalcUsado);
		mpmItemPrescricaoNpt.setUnidadeMedidaMedicas(this.mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(ummSeq));
		mpmItemPrescricaoNpt.setTotParamCalculo(totParam);
		mpmItemPrescricaoNpt.setPcnSeqp(pcnSeqp.shortValue());
		mpmItemPrescricaoNpt.setPcnCnpMedMatCodigo(mpmItemPrescricaoNpt.getAfaComponenteNpts().getMedMatCodigo());		
		if (paramPerc != null) {
			mpmItemPrescricaoNpt.setPercParamCalculo(paramPerc);
		}
	}
	
	private MpmItemPrescricaoNptVO parseVOComponente(MpmItemPrescricaoNpt ipn) throws IllegalAccessException, InvocationTargetException {
		MpmItemPrescricaoNptVO vo = new MpmItemPrescricaoNptVO();
		BeanUtils.copyProperties(vo, ipn);
		vo.setCptPnpAtdSeq(ipn.getId().getCptPnpAtdSeq());
		vo.setCptPnpSeq(ipn.getId().getCptPnpSeq());
		vo.setCptSeqp(ipn.getId().getCptSeqp());
		vo.setSeqp(ipn.getId().getSeqp());
		
		vo.setCnpMedMatCodigo(ipn.getAfaComponenteNpts().getMedMatCodigo());
		vo.setFdsSeq(ipn.getAfaFormaDosagens().getSeq());
		
		return vo;
	}
	
	private MpmComposicaoPrescricaoNptVO parseVOComposicao(MpmComposicaoPrescricaoNpt comp) throws IllegalAccessException, InvocationTargetException {
		MpmComposicaoPrescricaoNptVO vo = new MpmComposicaoPrescricaoNptVO();
		BeanUtils.copyProperties(vo, comp);
		vo.setPnpAtdSeq(comp.getId().getPnpAtdSeq());
		vo.setPnpSeq(comp.getId().getPnpSeq());
		vo.setSeqp(comp.getId().getSeqp());
		
		vo.setTicSeq(comp.getAfaTipoComposicoes().getSeq());
		vo.setVelocidadeAdministracao(comp.getVelocidadeAdministracao());
		
		return vo;
	}
	
	/**
	 * PC10 - @ORADB MPMP_POPULA_CALCULO_A - procedure responsável por gravar os dados calculados para prescrição NPT.
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void popularCalculoAdulto(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		
		this.criticarParametrosCalculoAdulto(calculoAdultoNptVO);
		this.criticarVolumesCalculoAdulto(calculoAdultoNptVO);
		this.moverAnaliseAdulto(prescricaoNptVo, calculoAdultoNptVO);

		// salva_dados_a; -- Commit. Persiste os dados em MpmPrescricaoNpt
		MpmPrescricaoNpt mpmPrescricaoNpt = this.parseVOtoMpmPrescricaoNpt(prescricaoNptVo, true);
		Date dataFimVinculoServidor = servidorLogadoFacade.obterServidorLogado().getDtFimVinculo();
		this.prescricaoNptRN.atualizar(mpmPrescricaoNpt, null, dataFimVinculoServidor);

		prescricaoNptVo = this.parseMpmPrescricaoNpttoVO(mpmPrescricaoNpt);
		
		this.inicializarControleAdulto(calculoAdultoNptVO);
		
		for (MpmComposicaoPrescricaoNptVO mpmComposicaoPrescricaoNptVO : prescricaoNptVo.getComposicoes()) {
			
			if (mpmComposicaoPrescricaoNptVO.getTicSeq().intValue() == calculoAdultoNptVO.getCalculoParametrosFixosVO().getComposicaoSolNpt()) {
				mpmComposicaoPrescricaoNptVO.setVelocidadeAdministracao(calculoAdultoNptVO.getGotejoSolucao());
			} else if (mpmComposicaoPrescricaoNptVO.getTicSeq().intValue() == calculoAdultoNptVO.getCalculoParametrosFixosVO().getComposicaoSolLipidios()) {
				mpmComposicaoPrescricaoNptVO.setVelocidadeAdministracao(calculoAdultoNptVO.getGotejoLipidios());
			}
			
			MpmComposicaoPrescricaoNpt mpmComposicaoPrescricaoNpt = this.parseVOtoMpmComposicaoPrescricaoNpt(mpmComposicaoPrescricaoNptVO, mpmPrescricaoNpt, true);
			
			for (MpmItemPrescricaoNptVO mpmItemPrescricaoNptVO : mpmComposicaoPrescricaoNptVO.getComponentes()) {
				
				switch (mpmItemPrescricaoNptVO.getIdentifComponente()) {
				
				case LIPIDIOS_10:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolLipidios10(), calculoAdultoNptVO.getVolLipidios10(), 
							calculoAdultoNptVO.getParamLip(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getUmmSeq(), calculoAdultoNptVO.getTotParamLip(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopLipidios10(Boolean.TRUE);
					break;
				
				case LIPIDIOS_20:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolLipidios20(), calculoAdultoNptVO.getVolLipidios20(), 
							calculoAdultoNptVO.getParamLip(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getUmmSeq(), calculoAdultoNptVO.getTotParamLip(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopLipidios20(Boolean.TRUE);
					break;
				
				case AMINOACIDOS_AD:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolAa10(), calculoAdultoNptVO.getVolAa10(), 
							calculoAdultoNptVO.getParamAmin(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAmin(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopAmin10(Boolean.TRUE);
					break;
				
				case AMINOACIDOS_PED:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolAaPed10(), calculoAdultoNptVO.getVolAaPed10(), 
							calculoAdultoNptVO.getParamAmin(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAmin(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopAminPed10(Boolean.TRUE);
					break;
				
				case ACETATO_ZINCO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolAcetZn(), calculoAdultoNptVO.getVolAcetZn(), 
							calculoAdultoNptVO.getParamAcetZn(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAcetZn(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopAcetZn(Boolean.TRUE);					
					break;
				
				case CLORETO_SODIO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolNacl20(), calculoAdultoNptVO.getVolNacl20(), 
							calculoAdultoNptVO.getParamSodio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamSodio(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopNacl20(Boolean.TRUE);
					break;
				
				case GLUCO_CALCIO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolGlucoCa(), calculoAdultoNptVO.getVolGlucoCa(), 
							calculoAdultoNptVO.getParamCalcio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamCalcio(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopGlucoCa(Boolean.TRUE);
					break;

				case CLORETO_POTASSIO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolKcl(), calculoAdultoNptVO.getVolKcl(), 
							calculoAdultoNptVO.getParamPotassio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamPotassio(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getPcnSeqp(), calculoAdultoNptVO.getParamPercKcl());
					calculoAdultoNptVO.setPopVolKcl(Boolean.TRUE);					
					break;
					
				case FOSFATO_POTASSIO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolK3Po4(), calculoAdultoNptVO.getVolK3Po4(), 
							calculoAdultoNptVO.getParamPotassio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamPotassio(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getPcnSeqp(), calculoAdultoNptVO.getParamPercK3po4());
					calculoAdultoNptVO.setPopK3PO4(Boolean.TRUE);
					break;
					
				case SULFATO_MAGNESIO:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolMgso4(), calculoAdultoNptVO.getVolMgso4(), 
							calculoAdultoNptVO.getParamMagnesio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamMagnesio(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopMGSO4(Boolean.TRUE);
					break;
					
				case GLICOSE_50:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolGlicose50(), calculoAdultoNptVO.getVolGlicose50(), 
							calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getPcnSeqp(), calculoAdultoNptVO.getParamPercGlic50());
					calculoAdultoNptVO.setPopGlicose50(Boolean.TRUE);
					break;
					
				case GLICOSE_10:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolGlicose10(), calculoAdultoNptVO.getVolGlicose10(), 
							calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getPcnSeqp(), calculoAdultoNptVO.getParamPercGlic10());
					calculoAdultoNptVO.setPopGlicose10(Boolean.TRUE);
					break;
					
				case GLICOSE_5:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolGlicose5(), calculoAdultoNptVO.getVolGlicose5(), 
							calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopGlicose5(Boolean.TRUE);
					break;
					
				case HEPARINA:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolHeparina(), calculoAdultoNptVO.getVolHeparina(), 
							calculoAdultoNptVO.getParamHeparina(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getUmmSeq(), calculoAdultoNptVO.getTotParamHeparina(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopHeparina(Boolean.TRUE); 	
					break;
					
				case OLIGOELEMENTOS_AD:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolOligo(), calculoAdultoNptVO.getVolOligo(), 
							calculoAdultoNptVO.getParamOligo(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getUmmSeq(), calculoAdultoNptVO.getTotParamOligo(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopOligo(Boolean.TRUE);	
					break;
					
				case OLIGOELEMENTOS_PED:
					this.moverComponente(mpmItemPrescricaoNptVO, calculoAdultoNptVO.getVolOligoPed(), calculoAdultoNptVO.getVolOligoPed(), 
							calculoAdultoNptVO.getParamOligo(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getTpParamCalcUsado(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getUmmSeq(), calculoAdultoNptVO.getTotParamOligo(), 
							calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getPcnSeqp(), null);
					calculoAdultoNptVO.setPopOligoPed(Boolean.TRUE);
					break;
				default:
					break;
				}
				
				MpmItemPrescricaoNpt item = parseVOtoMpmItemPrescricaoNpt(mpmItemPrescricaoNptVO, mpmComposicaoPrescricaoNpt, true);
				this.itemPrescricaoNptRN.atualizar(item);
				
				mpmItemPrescricaoNptVO = parseMpmItemPrescricaoNpttoVO(item);
			}
			
			this.composicaoPrescricaoNptRN.atualizar(mpmComposicaoPrescricaoNpt);
			
			mpmComposicaoPrescricaoNptVO = parseMpmComposicaoPrescricaoNpttoVO(mpmComposicaoPrescricaoNpt);
		}
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolLipidios10(), calculoAdultoNptVO.getPopLipidios10(), DominioIdentificacaoComponenteNPT.LIPIDIOS_10,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolLipidios10(), calculoAdultoNptVO.getVolLipidios10(), 
				calculoAdultoNptVO.getParamLip(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getUmmSeq(), calculoAdultoNptVO.getTotParamLip(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO().getPcnSeqp(), null); 
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolLipidios20(), calculoAdultoNptVO.getPopLipidios20(), DominioIdentificacaoComponenteNPT.LIPIDIOS_20,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolLipidios20(), calculoAdultoNptVO.getVolLipidios20(), 
				calculoAdultoNptVO.getParamLip(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getUmmSeq(), calculoAdultoNptVO.getTotParamLip(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolAa10(), calculoAdultoNptVO.getPopAmin10(), DominioIdentificacaoComponenteNPT.AMINOACIDOS_AD,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolAa10(), calculoAdultoNptVO.getVolAa10(), 
				calculoAdultoNptVO.getParamAmin(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAmin(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolAaPed10(), calculoAdultoNptVO.getPopAminPed10(), DominioIdentificacaoComponenteNPT.AMINOACIDOS_PED,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolAaPed10(), calculoAdultoNptVO.getVolAaPed10(), 
				calculoAdultoNptVO.getParamAmin(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAmin(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolAcetZn(), calculoAdultoNptVO.getPopAcetZn(), DominioIdentificacaoComponenteNPT.ACETATO_ZINCO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolAcetZn(), calculoAdultoNptVO.getVolAcetZn(), 
				calculoAdultoNptVO.getParamAcetZn(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getUmmSeq(), calculoAdultoNptVO.getTotParamAcetZn(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolNacl20(), calculoAdultoNptVO.getPopNacl20(), DominioIdentificacaoComponenteNPT.CLORETO_SODIO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolNacl20(), calculoAdultoNptVO.getVolNacl20(), 
				calculoAdultoNptVO.getParamSodio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamSodio(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolGlucoCa(), calculoAdultoNptVO.getPopGlucoCa(), DominioIdentificacaoComponenteNPT.GLUCO_CALCIO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolGlucoCa(), calculoAdultoNptVO.getVolGlucoCa(), 
				calculoAdultoNptVO.getParamCalcio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamCalcio(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolKcl(), calculoAdultoNptVO.getPopVolKcl(), DominioIdentificacaoComponenteNPT.CLORETO_POTASSIO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolKcl(), calculoAdultoNptVO.getVolKcl(), 
				calculoAdultoNptVO.getParamPotassio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamPotassio(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO().getPcnSeqp(), calculoAdultoNptVO.getParamPercKcl());
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolK3Po4(), calculoAdultoNptVO.getPopK3PO4(), DominioIdentificacaoComponenteNPT.FOSFATO_POTASSIO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolK3Po4(), calculoAdultoNptVO.getVolK3Po4(), 
				calculoAdultoNptVO.getParamPotassio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamPotassio(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getPcnSeqp(), calculoAdultoNptVO.getParamPercK3po4());
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolMgso4(), calculoAdultoNptVO.getPopMGSO4(), DominioIdentificacaoComponenteNPT.SULFATO_MAGNESIO,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolMgso4(), calculoAdultoNptVO.getVolMgso4(), 
				calculoAdultoNptVO.getParamMagnesio(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getUmmSeq(), calculoAdultoNptVO.getTotParamMagnesio(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolGlicose50(), calculoAdultoNptVO.getPopGlicose50(), DominioIdentificacaoComponenteNPT.GLICOSE_50,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolGlicose50(), calculoAdultoNptVO.getVolGlicose50(), 
				calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO().getPcnSeqp(), calculoAdultoNptVO.getParamPercGlic50());
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolGlicose10(), calculoAdultoNptVO.getPopGlicose10(), DominioIdentificacaoComponenteNPT.GLICOSE_10,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolGlicose10(), calculoAdultoNptVO.getVolGlicose10(), 
				calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO().getPcnSeqp(), calculoAdultoNptVO.getParamPercGlic10());
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolGlicose5(), calculoAdultoNptVO.getPopGlicose5(), DominioIdentificacaoComponenteNPT.GLICOSE_5,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolGlicose5(), calculoAdultoNptVO.getVolGlicose5(), 
				calculoAdultoNptVO.getParamTig(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getUmmSeq(), calculoAdultoNptVO.getTotParamTig(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolHeparina(), calculoAdultoNptVO.getPopHeparina(), DominioIdentificacaoComponenteNPT.HEPARINA,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolHeparina(), calculoAdultoNptVO.getVolHeparina(), 
				calculoAdultoNptVO.getParamHeparina(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getUmmSeq(), calculoAdultoNptVO.getTotParamHeparina(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolOligo(), calculoAdultoNptVO.getPopOligo(), DominioIdentificacaoComponenteNPT.OLIGOELEMENTOS_AD,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolOligo(), calculoAdultoNptVO.getVolOligo(), 
				calculoAdultoNptVO.getParamOligo(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getUmmSeq(), calculoAdultoNptVO.getTotParamOligo(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO().getPcnSeqp(), null);
		
		this.verificarCampoPreenchidoSemUpdate(calculoAdultoNptVO.getVolOligoPed(), calculoAdultoNptVO.getPopOligoPed(), DominioIdentificacaoComponenteNPT.OLIGOELEMENTOS_PED,
				prescricaoNptVo, calculoAdultoNptVO, calculoAdultoNptVO.getVolOligoPed(), calculoAdultoNptVO.getVolOligoPed(), 
				calculoAdultoNptVO.getParamOligo(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getTpParamCalcUsado(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getUmmSeq(), calculoAdultoNptVO.getTotParamOligo(), 
				calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO().getPcnSeqp(), null);

		calculoAdultoNptVO.getCalculoParametrosFixosVO().setPrescricaoCalculada(Boolean.TRUE);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setMoveComponentesCalculados(Boolean.FALSE);
	}
	
	private void verificarCampoPreenchidoSemUpdate(BigDecimal volume, Boolean atualizado, DominioIdentificacaoComponenteNPT identifComponente,
			MpmPrescricaoNptVO vo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal qtdeCalculada,
			BigDecimal qtdePrescrita, BigDecimal qtdeBaseCalculo, DominioTipoVolume tpParamCalcUsado, Integer ummSeq,
			BigDecimal totParam, Integer pcnSeqp, BigDecimal paramPerc) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		
		if (volume != null && volume.compareTo(BigDecimal.ZERO) > 0 && atualizado == Boolean.FALSE) {
			this.calcularNptRN.mpmpIncluiCompA(vo, identifComponente, calculoAdultoNptVO, qtdeCalculada, qtdePrescrita, qtdeBaseCalculo, 
					tpParamCalcUsado, ummSeq, totParam, pcnSeqp, paramPerc);
		}
	}
	
	protected void moverComponente(MpmItemPrescricaoNptVO mpmItemPrescricaoNptVO, BigDecimal qtdeCalculada,
			BigDecimal qtdePrescrita, BigDecimal qtdeBaseCalculo, DominioTipoVolume tpParamCalcUsado, Integer ummSeq,
			BigDecimal totParam, Integer pcnSeqp, BigDecimal paramPerc) {		
		mpmItemPrescricaoNptVO.setQtdeCalculada(qtdeCalculada);
		mpmItemPrescricaoNptVO.setQtdePrescrita(qtdePrescrita);
		mpmItemPrescricaoNptVO.setQtdeBaseCalculo(qtdeBaseCalculo);
		mpmItemPrescricaoNptVO.setTipoParamCalculo(tpParamCalcUsado);
		mpmItemPrescricaoNptVO.setUmmSeq(ummSeq);
		mpmItemPrescricaoNptVO.setTotParamCalculo(totParam);
		mpmItemPrescricaoNptVO.setPcnSeqp(pcnSeqp.shortValue());
		mpmItemPrescricaoNptVO.setPcnCnpMedMatCodigo(mpmItemPrescricaoNptVO.getCnpMedMatCodigo());
		
		if (paramPerc != null) {
			mpmItemPrescricaoNptVO.setPercParamCalculo(paramPerc);
		}
	}
	
	/**
	 * @ORADB PROCEDURE INICIALIZA_CONTROLE_A
	 * 
	 * @param calculoAdultoNptVO
	 */
	private void inicializarControleAdulto(CalculoAdultoNptVO calculoAdultoNptVO) {		
		calculoAdultoNptVO.setPopLipidios10(Boolean.FALSE);
		calculoAdultoNptVO.setPopLipidios20(Boolean.FALSE);
		calculoAdultoNptVO.setPopAmin10(Boolean.FALSE);
		calculoAdultoNptVO.setPopAminPed10(Boolean.FALSE);
		calculoAdultoNptVO.setPopAcetZn(Boolean.FALSE);
		calculoAdultoNptVO.setPopNacl20(Boolean.FALSE);
		calculoAdultoNptVO.setPopGlucoCa(Boolean.FALSE);
		calculoAdultoNptVO.setPopVolKcl(Boolean.FALSE);
		calculoAdultoNptVO.setPopK3PO4(Boolean.FALSE);
		calculoAdultoNptVO.setPopMGSO4(Boolean.FALSE);
		calculoAdultoNptVO.setPopGlicose50(Boolean.FALSE);
		calculoAdultoNptVO.setPopGlicose10(Boolean.FALSE);
		calculoAdultoNptVO.setPopGlicose5(Boolean.FALSE);
		calculoAdultoNptVO.setPopHeparina(Boolean.FALSE);
		calculoAdultoNptVO.setPopOligo(Boolean.FALSE);
		calculoAdultoNptVO.setPopOligoPed(Boolean.FALSE);
	}
	
	/**
	 * PROCEDURE MOVE_ANALISE_A
	 * @param prescricaoNptVO
	 * @param calculoAdultoNptVO
	 */
	private void moverAnaliseAdulto(MpmPrescricaoNptVO prescricaoNptVO, CalculoAdultoNptVO calculoAdultoNptVO) {		
		prescricaoNptVO.setParamVolumeMl(calculoAdultoNptVO.getParamVolDes().doubleValue());
		prescricaoNptVO.setVolumeDesejado(calculoAdultoNptVO.getTotParamVolDes().doubleValue());
		prescricaoNptVO.setVolumeCalculado(calculoAdultoNptVO.getTotParamVolCalc().doubleValue());
		prescricaoNptVO.setTipoParamVolume(calculoAdultoNptVO.getCalculoParametrosFixosVO().getTipoParamCalculoVol());
		prescricaoNptVO.setParamTipoLipidio(calculoAdultoNptVO.getParamTipoLip());
		prescricaoNptVO.setTempoHInfusaoSolucao(calculoAdultoNptVO.getParamTempInfusaoSol());
		prescricaoNptVO.setTempoHInfusaoLipidios(calculoAdultoNptVO.getParamTempInfusaoLip());
		prescricaoNptVO.setPcaAtdSeq(prescricaoNptVO.getAtdSeq());
		prescricaoNptVO.setPcaCriadoEm(calculoAdultoNptVO.getDadosPesoAlturaVO().getPcaCriadoEm());
		prescricaoNptVO.setCaloriasDia(calculoAdultoNptVO.getCaloriasDia().doubleValue());
		prescricaoNptVO.setCaloriasKgDia(calculoAdultoNptVO.getCaloriasKgDia().doubleValue());
		prescricaoNptVO.setRelCalNProtNitrogenio(calculoAdultoNptVO.getRelCalGNitro().doubleValue());
		prescricaoNptVO.setPercCalAminoacidos(calculoAdultoNptVO.getPercCalAmin().doubleValue());
		prescricaoNptVO.setPercCalLipidios(calculoAdultoNptVO.getPercCalLipidios().doubleValue());
		prescricaoNptVO.setPercCalGlicose(calculoAdultoNptVO.getPercCalGlicose().doubleValue());
		prescricaoNptVO.setLipidiosRelGlicLipid(calculoAdultoNptVO.getRelGlicoseLipiL().doubleValue());
		prescricaoNptVO.setGlicoseRelGlicLipid(calculoAdultoNptVO.getRelGlicoseLipiG().doubleValue());
		prescricaoNptVO.setRelacaoCalcioFosforo(calculoAdultoNptVO.getRelCalcioFosforo().doubleValue());
		prescricaoNptVO.setConcGlicSemLipidios(calculoAdultoNptVO.getConcGlicoseSemLipi().doubleValue());
		prescricaoNptVO.setConcGlicComLipidios(calculoAdultoNptVO.getConcGlicoseComLipi().doubleValue());
		prescricaoNptVO.setTaxaInfusaoLipidios(calculoAdultoNptVO.getTaxaInfusaoLipi().doubleValue());
		prescricaoNptVO.setOsmolaridadeSemLipidios(calculoAdultoNptVO.getOsmolSemLipi().doubleValue());
		prescricaoNptVO.setOsmolaridadeComLipidios(calculoAdultoNptVO.getOsmolComLipi().doubleValue());
	}
	
	/**
	 * @ORADB CRITICA_PARAMETROS_A
	 * 
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void criticarParametrosCalculoAdulto(CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamVolDes(), CalcularNptONExceptionCode.MPM_03478);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getTotParamVolDes(), CalcularNptONExceptionCode.MPM_03479);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamLip(), CalcularNptONExceptionCode.MPM_03480);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamAmin(), CalcularNptONExceptionCode.MPM_03481);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamSodio(), CalcularNptONExceptionCode.MPM_03482);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamCalcio(), CalcularNptONExceptionCode.MPM_03483);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamMagnesio(), CalcularNptONExceptionCode.MPM_03484);		
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamOligo(), CalcularNptONExceptionCode.MPM_03485);		
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamPotassio(), CalcularNptONExceptionCode.MPM_03486);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamAcetZn(), CalcularNptONExceptionCode.MPM_03487);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamTig(), CalcularNptONExceptionCode.MPM_03488);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getParamHeparina(), CalcularNptONExceptionCode.MPM_03489);
		this.verificarCampoZeradoOuNegativo(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoSol()), CalcularNptONExceptionCode.MPM_03503);
		this.verificarCampoZeradoOuNegativo(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoLip()), CalcularNptONExceptionCode.MPM_03504);
	}
	
	/**
	 * @ORADB CRITICA_VOLUMES_A
	 * 
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void criticarVolumesCalculoAdulto(CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {		
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolNacl20(), CalcularNptONExceptionCode.MPM_03490);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolGlucoCa(), CalcularNptONExceptionCode.MPM_03491);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolKcl(), CalcularNptONExceptionCode.MPM_03492);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolK3Po4(), CalcularNptONExceptionCode.MPM_03493);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolMgso4(), CalcularNptONExceptionCode.MPM_03494);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolOligo(), CalcularNptONExceptionCode.MPM_03495);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolOligoPed(), CalcularNptONExceptionCode.MPM_03496);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolLipidios10(), CalcularNptONExceptionCode.MPM_03497);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolLipidios20(), CalcularNptONExceptionCode.MPM_03498);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolGlicose5(), CalcularNptONExceptionCode.MPM_03499);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolGlicose10(), CalcularNptONExceptionCode.MPM_03500);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolGlicose50(), CalcularNptONExceptionCode.MPM_03501);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolHeparina(), CalcularNptONExceptionCode.MPM_03502);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getGotejoSolucao(), CalcularNptONExceptionCode.MPM_03505);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getGotejoLipidios(), CalcularNptONExceptionCode.MPM_03506);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolAa10(), CalcularNptONExceptionCode.MPM_03507);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolAaPed10(), CalcularNptONExceptionCode.MPM_03508);
		this.verificarCampoZeradoOuNegativo(calculoAdultoNptVO.getVolAcetZn(), CalcularNptONExceptionCode.MPM_03509);
	}
	
	private void verificarCampoZeradoOuNegativo(BigDecimal valor, CalcularNptONExceptionCode mensagem) 
			throws ApplicationBusinessException {
		if (valor == BigDecimal.ZERO || valor.compareTo(BigDecimal.ZERO) == -1) {
			throw new ApplicationBusinessException(mensagem);
		}
	}
	
	protected MpmPrescricaoNpt parseVOtoMpmPrescricaoNpt(MpmPrescricaoNptVO vo, Boolean isUpdate) throws ApplicationBusinessException,
			IllegalAccessException, InvocationTargetException {
		MpmPrescricaoNpt prescricao = new MpmPrescricaoNpt();
		if (!isUpdate) {
			prescricao.setId(new MpmPrescricaoNptId(vo.getAtdSeq(),	mpmPrescricaoNptDAO.getNextVal(SequenceID.MPM_PNP_SQ1)));
		} else {
			prescricao = mpmPrescricaoNptDAO.obterPorChavePrimaria(new MpmPrescricaoNptId(vo.getAtdSeq(), vo.getSeq()));
		}

		BeanUtils.copyProperties(prescricao, vo);
		prescricao.setAfaFormulaNptPadrao(afaFormulaNptPadraoDAO.obterOriginal(vo.getFnpSeq()));
		prescricao.setProcedEspecialDiversos(mpmProcedEspecialDiversoDAO.obterPeloId(vo.getPedSeq()));
		prescricao.setPrescricaoMedica(vo.getPrescricaoMedica());
		if (vo.getPnpSeq() != null && vo.getPnpAtdSeq() != null) {
			MpmPrescricaoNpt origem = mpmPrescricaoNptDAO.obterPorChavePrimaria(new MpmPrescricaoNptId(vo.getPnpAtdSeq(), vo.getPnpSeq()));
			prescricao.setPrescricaoNpts(origem);
		}
		return prescricao;
	}
	
	protected MpmPrescricaoNptVO parseMpmPrescricaoNpttoVO(MpmPrescricaoNpt mpmPrescricaoNpt) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MpmPrescricaoNptVO vo = new MpmPrescricaoNptVO();
		BeanUtils.copyProperties(vo, mpmPrescricaoNpt);
		
		if (mpmPrescricaoNpt.getAfaFormulaNptPadrao() != null) {
			vo.setFnpSeq(mpmPrescricaoNpt.getAfaFormulaNptPadrao().getSeq());
		}
		vo.setPedSeq(mpmPrescricaoNpt.getProcedEspecialDiversos().getSeq());
		vo.setPrescricaoMedica(mpmPrescricaoNpt.getPrescricaoMedica());
		
		if (mpmPrescricaoNpt.getPrescricaoNpts() != null && mpmPrescricaoNpt.getPrescricaoNpts().getId() != null) {
			vo.setPnpSeq(mpmPrescricaoNpt.getPrescricaoNpts().getId().getSeq());
			vo.setPnpAtdSeq(mpmPrescricaoNpt.getPrescricaoNpts().getId().getAtdSeq());
		}
		return vo;
	}
	
	protected MpmItemPrescricaoNpt parseVOtoMpmItemPrescricaoNpt(MpmItemPrescricaoNptVO mpmItemPrescricaoNptVO, MpmComposicaoPrescricaoNpt composicao, 
			Boolean isUpdate) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MpmItemPrescricaoNpt entity = new MpmItemPrescricaoNpt();
		
		if (!isUpdate) {
			Short seqp = mpmItemPrescricaoNptDAO.obterUltimoSeqpComponente(composicao.getId().getPnpAtdSeq(), composicao.getId().getPnpSeq(), composicao.getId().getSeqp());
			entity.setId(new MpmItemPrescricaoNptId(composicao.getId().getPnpAtdSeq(), composicao.getId().getPnpSeq(), composicao.getId().getSeqp(), (short) (seqp + Short.valueOf("1"))));
		} else {
			MpmItemPrescricaoNptId id = new MpmItemPrescricaoNptId(mpmItemPrescricaoNptVO.getCptPnpAtdSeq(), mpmItemPrescricaoNptVO.getCptPnpSeq(), 
					mpmItemPrescricaoNptVO.getCptSeqp(), mpmItemPrescricaoNptVO.getSeqp());
			entity = this.mpmItemPrescricaoNptDAO.obterPorChavePrimaria(id);
		}		
		BeanUtils.copyProperties(entity, mpmItemPrescricaoNptVO);
		entity.setMpmComposicaoPrescricaoNpts(composicao);
		entity.setAfaComponenteNpts(afaComponenteNptDAO.obterPorChavePrimaria(mpmItemPrescricaoNptVO.getCnpMedMatCodigo()));
		entity.setAfaFormaDosagens(afaFormaDosagemDAO.obterPorChavePrimaria(mpmItemPrescricaoNptVO.getFdsSeq()));
		entity.setUnidadeMedidaMedicas(mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(mpmItemPrescricaoNptVO.getUmmSeq()));
		return entity;
	}
	
	protected MpmItemPrescricaoNptVO parseMpmItemPrescricaoNpttoVO(MpmItemPrescricaoNpt mpmItemPrescricaoNpt) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MpmItemPrescricaoNptVO vo = new MpmItemPrescricaoNptVO();
		BeanUtils.copyProperties(vo, mpmItemPrescricaoNpt);		
		vo.setCptPnpAtdSeq(mpmItemPrescricaoNpt.getMpmComposicaoPrescricaoNpts().getId().getPnpAtdSeq());
		vo.setCptPnpSeq(mpmItemPrescricaoNpt.getMpmComposicaoPrescricaoNpts().getId().getPnpSeq());
		vo.setCptSeqp(mpmItemPrescricaoNpt.getMpmComposicaoPrescricaoNpts().getId().getSeqp());
		vo.setCnpMedMatCodigo(mpmItemPrescricaoNpt.getAfaComponenteNpts().getMedMatCodigo());
		vo.setFdsSeq(mpmItemPrescricaoNpt.getAfaFormaDosagens().getSeq());
		vo.setUmmSeq(mpmItemPrescricaoNpt.getUnidadeMedidaMedicas().getSeq());		
		return vo;
	}
	
	protected MpmComposicaoPrescricaoNpt parseVOtoMpmComposicaoPrescricaoNpt(MpmComposicaoPrescricaoNptVO item, MpmPrescricaoNpt prescricao, Boolean isUpdate) 
			throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MpmComposicaoPrescricaoNpt entity = new MpmComposicaoPrescricaoNpt();
		
		if (!isUpdate) {
			Short seqp = composicaoPrescricaoNptDAO.obterUltimoSeqpComposicao(prescricao.getId().getSeq(), prescricao.getId().getAtdSeq());
			entity.setId(new MpmComposicaoPrescricaoNptId(prescricao.getId().getAtdSeq(),prescricao.getId().getSeq(), (short) (seqp + Short.valueOf("1"))));
		} else {
			entity = composicaoPrescricaoNptDAO.obterPorChavePrimaria(new MpmComposicaoPrescricaoNptId(item.getPnpAtdSeq(), item.getPnpSeq(), item.getSeqp()));
		}
		BeanUtils.copyProperties(entity, item);
		entity.setAfaTipoComposicoes(afaTipoComposicoesDAO.obterPorChavePrimaria(item.getTicSeq()));
		entity.setAfaTipoVelocAdministracoes(afaTipoVelocAdministracoesDAO.obterPorChavePrimaria(item.getTvaSeq()));
		entity.setMpmPrescricaoNpts(prescricao);
		return entity;
	}
	
	protected MpmComposicaoPrescricaoNptVO parseMpmComposicaoPrescricaoNpttoVO(MpmComposicaoPrescricaoNpt mpmComposicaoPrescricaoNpt) 
			throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MpmComposicaoPrescricaoNptVO vo = new MpmComposicaoPrescricaoNptVO();
		BeanUtils.copyProperties(vo, mpmComposicaoPrescricaoNpt);
		vo.setTicSeq(mpmComposicaoPrescricaoNpt.getAfaTipoComposicoes().getSeq());
		vo.setTvaSeq(mpmComposicaoPrescricaoNpt.getAfaTipoVelocAdministracoes().getSeq());
		vo.setPnpAtdSeq(mpmComposicaoPrescricaoNpt.getMpmPrescricaoNpts().getId().getAtdSeq());
		vo.setPnpSeq(mpmComposicaoPrescricaoNpt.getMpmPrescricaoNpts().getId().getSeq());
		return vo;
	}
	
	/**
	 * PC12 - responsável por copiar os valores dos VO’s MpmComposicaoPrescricaoNptVO e MpmItemPrescricaoNptVO para o CalculoParametrosFixosVO.
	 * 
	 * @param calculoAdultoNptVO
	 * @param prescricaoNptVO
	 */
	public void popularCalculoParametrosFixos(CalculoAdultoNptVO calculoAdultoNptVO, MpmPrescricaoNptVO prescricaoNptVO) {
		
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoSolucao(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios10(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios20(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAaPed10(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAcetZn(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolNacl20(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlucoCa(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolKcl(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolK3po4(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolMsgo4(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose50(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose10(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose5(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolHeparina(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolOligo(null);
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolOligoPed(null);
		
		for (MpmComposicaoPrescricaoNptVO mpmComposicaoPrescricaoNptVO : prescricaoNptVO.getComposicoes()) {
			
			if (mpmComposicaoPrescricaoNptVO.getTicSeq().intValue() == calculoAdultoNptVO.getCalculoParametrosFixosVO().getComposicaoSolNpt()) {
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoSolucao(mpmComposicaoPrescricaoNptVO.getVelocidadeAdministracao().doubleValue());
			} else if (mpmComposicaoPrescricaoNptVO.getTicSeq().intValue() == calculoAdultoNptVO.getCalculoParametrosFixosVO().getComposicaoSolLipidios()) {
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(mpmComposicaoPrescricaoNptVO.getVelocidadeAdministracao().doubleValue());
			}
			
			for (MpmItemPrescricaoNptVO mpmItemPrescricaoNptVO : mpmComposicaoPrescricaoNptVO.getComponentes()) {
				
				switch (mpmItemPrescricaoNptVO.getIdentifComponente()) {
				
				case LIPIDIOS_10:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios10(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case LIPIDIOS_20:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios20(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case AMINOACIDOS_AD:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case AMINOACIDOS_PED:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAaPed10(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case ACETATO_ZINCO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAcetZn(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case CLORETO_SODIO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolNacl20(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;				
				case GLUCO_CALCIO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlucoCa(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;
				case CLORETO_POTASSIO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolKcl(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case FOSFATO_POTASSIO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolK3po4(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case SULFATO_MAGNESIO:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolMsgo4(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case GLICOSE_50:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose50(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case GLICOSE_10:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose10(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case GLICOSE_5:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose5(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case HEPARINA:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolHeparina(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case OLIGOELEMENTOS_AD:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolOligo(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;					
				case OLIGOELEMENTOS_PED:
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolOligoPed(mpmItemPrescricaoNptVO.getQtdePrescrita().doubleValue());
					break;
				default:
					break;
				}				
			}			
		}		
	}
	
//	public void mpmpCalculaAnalise(CalculoAdultoNptVO calculoAdultoNptVO) {
//		BigDecimal vVolParaCalcGlcose, vVolDispSolucao, vVolDispMenosGli, vVolSolucao, vVolLipidios, vGGlicoseDia, vCalTotais, 
//			vRel1gNitroAmin, vGNitroAmin, vVolTotal, vTempoIngusaoLipi, vQtdeUnidLipidios, vRelCalcioFosforo, vMosmSolucao, 
//			MosmLipidios, vPercKcl, vTig;
//		
//		BigDecimal vCalAmin = BigDecimal.ZERO; 
//		BigDecimal vCalLipidios = BigDecimal.ZERO; 
//		BigDecimal vCalGlicose = BigDecimal.ZERO; 
//		BigDecimal vConcGlicose = BigDecimal.ZERO;
//		BigDecimal vParamKcl = BigDecimal.ZERO;
//		BigDecimal vParamKpo = BigDecimal.ZERO;
//		BigDecimal vTotParamKcl = BigDecimal.ZERO;
//		BigDecimal vTotParamKpo = BigDecimal.ZERO;
//		BigDecimal vVolCalculoReversoHep = BigDecimal.ZERO;
//		
//		Boolean vCalculoReversoTig = Boolean.FALSE;
//		Boolean vCalculoReversoHep = Boolean.FALSE;
//		
//		BigDecimal pVolAa10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAa10());
//		BigDecimal pVolAaPed10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAaPed10());
//		BigDecimal pVolAcetZn = this.obterValorNotNull(calculoAdultoNptVO.getVolAcetZn());
//		BigDecimal pVolNacl20 = this.obterValorNotNull(calculoAdultoNptVO.getVolNacl20());
//		BigDecimal pVolGlucoCa = this.obterValorNotNull(calculoAdultoNptVO.getVolGlucoCa());
//		BigDecimal pVolKcl = this.obterValorNotNull(calculoAdultoNptVO.getVolKcl());
//		BigDecimal pVolK3po4 = this.obterValorNotNull(calculoAdultoNptVO.getVolK3Po4());
//		BigDecimal pVolMgso4 = this.obterValorNotNull(calculoAdultoNptVO.getVolMgso4());
//		BigDecimal pVolGlicose5 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose5());
//		BigDecimal pVolGlicose10 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose10());
//		BigDecimal pVolGlicose50 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose50());
//		BigDecimal pVolOligo = this.obterValorNotNull(calculoAdultoNptVO.getVolOligo());
//		BigDecimal pVolOligoPed = this.obterValorNotNull(calculoAdultoNptVO.getVolOligoPed());
//		BigDecimal pVolLipidios10 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios10());
//		BigDecimal pVolLipidios20 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios20());
//
//		vVolSolucao = pVolAa10.add(pVolAaPed10).add(pVolAcetZn).add(pVolNacl20).add(pVolGlucoCa)
//				.add(pVolKcl).add(pVolK3po4).add(pVolMgso4).add(pVolGlicose5).add(pVolGlicose10).add(pVolGlicose50)
//				.add(pVolOligo).add(pVolOligoPed);
//		
//		vVolLipidios = pVolLipidios10.add(pVolLipidios20);
//		
//		if (pVolLipidios10.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO lip10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
//			vCalLipidios = vCalLipidios.add(pVolLipidios10.multiply(new BigDecimal(lip10VO.getConvMl()))
//					.multiply(new BigDecimal(lip10VO.getConvCalorias())));
//		}
//		
//		if (pVolLipidios20.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO lip20VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO();
//			vCalLipidios = vCalLipidios.add(pVolLipidios20.multiply(new BigDecimal(lip20VO.getConvMl()))
//					.multiply(new BigDecimal(lip20VO.getConvCalorias())));
//		}
//		
//		if (pVolAa10.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO aminAd = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
//			vCalAmin = vCalAmin.add(pVolAa10.multiply(new BigDecimal(aminAd.getConvMl()))
//					.multiply(new BigDecimal(aminAd.getConvCalorias())));
//		}
//		
//		if (pVolAaPed10.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO aminPed = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
//			vCalAmin = vCalAmin.add(pVolAaPed10.multiply(new BigDecimal(aminPed.getConvMl()))
//					.multiply(new BigDecimal(aminPed.getConvCalorias())));
//		}
//		
//		if (pVolGlicose5.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO glicose5 = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO();
//			vCalGlicose = vCalGlicose.add(pVolGlicose5.multiply(new BigDecimal(glicose5.getConvMl()))
//					.multiply(new BigDecimal(glicose5.getConvCalorias())));
//			
//			vConcGlicose = vConcGlicose.add(pVolGlicose5.multiply(new BigDecimal(glicose5.getConvMl())));
//		}
//		
//		if (pVolGlicose10.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptVO glicose10 = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO();
//			vCalGlicose = vCalGlicose.add(pVolGlicose10.multiply(new BigDecimal(glicose10.getConvMl()))
//					.multiply(new BigDecimal(glicose10.getConvCalorias())));
//			
//			vConcGlicose = vConcGlicose.add(pVolGlicose10.multiply(new BigDecimal(glicose10.getConvMl())));
//		}
//		
//		if (pVolGlicose50.compareTo(BigDecimal.ZERO) > 0) {
//			AfaParamNptGlicose50VO glicose50 = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
//			vCalGlicose = vCalGlicose.add(pVolGlicose50.multiply(new BigDecimal(glicose50.getConvMl()))
//					.multiply(new BigDecimal(glicose50.getConvCalorias())));
//			
//			vConcGlicose = vConcGlicose.add(pVolGlicose50.multiply(new BigDecimal(glicose50.getConvMl())));
//		}
//
//
//	}
//	
//	private BigDecimal obterValorNotNull(BigDecimal valor) {
//		return valor != null ? valor : BigDecimal.ZERO;
//	}
}
