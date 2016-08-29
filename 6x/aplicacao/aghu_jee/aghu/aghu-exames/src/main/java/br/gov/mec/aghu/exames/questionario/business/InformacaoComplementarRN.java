package br.gov.mec.aghu.exames.questionario.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.questionario.vo.VFatProcedSusPhiVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class InformacaoComplementarRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(InformacaoComplementarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1303144252802249641L;

	/**
	 * ORADB
	 * FUNCTION AELC_GET_SER_CONS
	 * @param filtro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterNomeProfissionalConselho(Integer matricula, Short vinCodigo) throws ApplicationBusinessException  {
		VRapServCrmAelVO vRapServCrmAelVO = new VRapServCrmAelVO();
		vRapServCrmAelVO.setMatricula(matricula);
		vRapServCrmAelVO.setVinCodigo(vinCodigo);
		List<VRapServCrmAelVO> lista = this.pesquisarViewRapServCrmAelVO(vRapServCrmAelVO);
		if(lista!=null && lista.size()>0){
			vRapServCrmAelVO = lista.get(0);
			StringBuilder retorno = new StringBuilder();
			if(vRapServCrmAelVO!=null) {
				String nome;
				if(vRapServCrmAelVO.getNome().length() > 50) {
					nome = vRapServCrmAelVO.getNome().substring(0,50);
				} else {
					nome = vRapServCrmAelVO.getNome();
				}
				retorno.append(nome);
				if(StringUtils.isNotBlank(vRapServCrmAelVO.getNroRegConselho())){
					retorno.append(" - crm");
					retorno.append(vRapServCrmAelVO.getNroRegConselho());
				}
			}
			return retorno.toString();	
		} else {
			return "";
		}
		
	}
	

	/**
	 * @ORADB View V_RAP_SERV_CRM_AEL
	 * 
	 */
	public List<VRapServCrmAelVO> pesquisarViewRapServCrmAelVO(VRapServCrmAelVO filtro) throws ApplicationBusinessException {
		String[] parametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FAT_SIGLAS_CONSELHOS_PROFISSIONAIS).getVlrTexto().split(",");
		return getRegistroColaboradorFacade().pesquisarViewRapServCrmAelVO(filtro, parametros);
	}

	
	/**
	 * @ORADB FUNCTION MPMC_VER_CONVENIO
	 * 
	 */
	public String obterConvenioAtendimento(Integer atdSeq){
		String retorno = "";
		AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		if(atendimento!=null && atendimento.getInternacao()!=null){
			AinInternacao internacao = this.getInternacaoFacade().obterInternacaoConvenio(atendimento.getInternacao().getSeq());
			retorno = internacao.getConvenioSaude().getDescricao();
		} else if(atendimento!=null && atendimento.getAtendimentoUrgencia()!=null){
			AinAtendimentosUrgencia atendimentoUrgencia = this.getInternacaoFacade().obterAtendimentoUrgenciaConvenio(atendimento.getAtendimentoUrgencia().getSeq());
			retorno = atendimentoUrgencia.getConvenioSaude().getDescricao();
		} else if(atendimento!=null && atendimento.getHospitalDia()!=null){
			AhdHospitaisDia hospitalDia = this.getInternacaoFacade().obterHospitalDiaConvenio(atendimento.getHospitalDia().getSeq());
			retorno = hospitalDia.getConvenioSaude().getDescricao();
		}
		return retorno;
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB FUNCTION AELC_BUSCA_COD_SUS
	 * 
	 */
	public Long obterCodigoSus(String sigla, Integer manSeq, DominioGrupoConvenio grupoConvenio, DominioOrigemAtendimento origem) throws ApplicationBusinessException{
		if(DominioGrupoConvenio.S.equals(grupoConvenio)){
			if(!DominioOrigemAtendimento.I.equals(origem)){
				return this.obterCodigoSusPorSiglaMaterial(sigla, manSeq);
			}
		}
		return Long.valueOf(0);
	}
	
	private Long obterCodigoSusPorSiglaMaterial(String sigla, Integer manSeq) throws ApplicationBusinessException{
		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setExaSigla(sigla);
		id.setManSeq(manSeq);
		
		FatProcedHospInternos procedimentoHospInterno = this.getFaturamentoFacade().obterFatProcedHospInternosPorMaterial(id);
		if(procedimentoHospInterno!=null){
			VFatProcedSusPhiVO vo = new VFatProcedSusPhiVO();
			vo.setPhiSeq(procedimentoHospInterno.getSeq());
			List<VFatProcedSusPhiVO> lista = this.pesquisarViewFatProcedSusPhiVO(vo);
			if(lista!=null && lista.size()>0){
				VFatProcedSusPhiVO vFatProcedSusPhiVO = lista.get(0);
				return vFatProcedSusPhiVO.getCodTabela();
			}
		}
		return Long.valueOf(0);
	}
	
	
	/**
	 * @ORADB View V_FAT_PROCED_SUS_PHI
	 * 
	 */
	public List<VFatProcedSusPhiVO> pesquisarViewFatProcedSusPhiVO(VFatProcedSusPhiVO filtro) throws ApplicationBusinessException {
		BigDecimal pSusPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico();
		Short parametro1 = null;
		if(pSusPadrao!=null){
			parametro1 = Short.valueOf(pSusPadrao.toString());
		}
		BigDecimal pSusAmbulatorio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico();
		Byte parametro2 = null;
		if(pSusAmbulatorio!=null){
			parametro2 = Byte.valueOf(pSusAmbulatorio.toString());
		}
		BigDecimal pTipoGrupoContaSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico();
		Short parametro3 = null;
		if(pTipoGrupoContaSus!=null){
			parametro3 = Short.valueOf(pTipoGrupoContaSus.toString());
		}
		
		List<VFatProcedSusPhiVO> listaResultados = getFaturamentoFacade().pesquisarViewFatProcedSusPhiVO(filtro, parametro1, parametro2, parametro3);
		
		for(VFatProcedSusPhiVO item : listaResultados) {
			item.setProcedPrincipal(getFaturamentoFacade().verificarCaracteristicaExame(item.getIphSeq(), item.getIphPhoSeq(),
					DominioFatTipoCaractItem.PROCEDIMENTO_PRINCIPAL_APAC));
			item.setProcedSecundario(getFaturamentoFacade().verificarCaracteristicaExame(item.getIphSeq(), item.getIphPhoSeq(),
					DominioFatTipoCaractItem.PROCEDIMENTO_SECUNDARIO_APAC));
		}
		
		return listaResultados;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
}
