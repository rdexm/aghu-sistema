package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOcorrFichaFluido;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFluidoAdministrado;
import br.gov.mec.aghu.dominio.DominioTipoFluidoPerdido;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.MbcFichaFarmaco;
import br.gov.mec.aghu.model.MbcFichaFluidoAdministrados;
import br.gov.mec.aghu.model.MbcFichaFluidoPerdido;
import br.gov.mec.aghu.model.MbcFichaOrgaoTransplante;
import br.gov.mec.aghu.model.MbcFichaTecnicaEspecial;
import br.gov.mec.aghu.model.MbcMonitorizacao;
import br.gov.mec.aghu.model.MbcOcorrFichaFluidoAdms;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaEvento;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemMonitorizacaoDefinidoFichaAnestVO;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAtosAnestesicosPOLRN extends BaseBusiness{

private static final String PONTO_VIRGULA____ = ";   ";

private static final Log LOG = LogFactory.getLog(RelatorioAtosAnestesicosPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastrosBasicosFacade cadastrosBasicosFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = 4855772824096688015L;

	/**
	 * @ORADB MBCC_IDA_ANO_MES_DIA
	 * Retorna a idade
		Se ANOS > zero,
			retorna somente anos e meses;
		senao,
			meses e dias;
		fim se;
	 */
	public String mbccIdaAnoMesDia(Integer pacCodigo){
		AipPacientes pac = getPacienteFacade().obterNomePacientePorCodigo(pacCodigo);
		return pac.getIdadeAnoMesFormat().replace(" 0 meses", "");
	}
	
	public IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	/**
	 * @ORADB MBCC_GET_DTHR_EVENTO
	 * Evento ANESTESIA ou CIRURGIA
	 * @return
	 *  
	 */
	public Date mbccGetDthrEvento(Long seqFichaAnestesia,
			DominioOcorrenciaFichaEvento fichaEvento, String evento) throws ApplicationBusinessException {
		AghParametros parametro = null;
		if("ANESTESIA".equals(evento.toUpperCase())){
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EVENTO_ANESTESIA);
		}else if("CIRURGIA".equals(evento.toUpperCase())){
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EVENTO_CIRURGIA);
		}
		
		BigDecimal seqMbcEventoPrincipal = parametro.getVlrNumerico();
		MbcOcorrenciaFichaEvento ocorrEvento = getBlocoCirurgicoFacade().obterMbcOcorrenciaFichaEventosComFicha(seqFichaAnestesia, fichaEvento, seqMbcEventoPrincipal.shortValue());
		return ocorrEvento != null ? ocorrEvento.getDthrOcorrencia(): null;
	}
	
	/**
	 * @ORADB MBCC_GET_DEF_MONIT
	 * @param seqMbcFichaAnestesia
	 * @return
	 */
	public String mbccGetDefMonit(Long seqMbcFichaAnestesia){
		List<MbcMonitorizacao> monitorizacoes = getBlocoCirurgicoFacade().pesquisarMbcMonitorizacoesComFichaAnestesia(seqMbcFichaAnestesia, Boolean.TRUE);
		StringBuilder txt = null;
		for(MbcMonitorizacao mon : monitorizacoes){
			if(txt == null){
				txt = new StringBuilder();
			}else{
				txt.append(", ");
			}
			
			txt.append(mon.getNome());
		}
		return txt != null ? txt.toString(): null;
	}
	
	/**
	 * @ORADB MBCC_GET_FARMACO
	 * @param seqMbcFichaFarmaco
	 */
	public String mbccGetFarmaco(Integer seqMbcFichaFarmaco){
		
		MbcFichaFarmaco fichaFarmaco = getBlocoCirurgicoFacade().obterMbcFichaFarmaco(seqMbcFichaFarmaco);
		
		if (fichaFarmaco != null){
			AfaGrupoMedicamento grupoMedicamento = getFarmaciaFacade()
			.obterAfaGrupoMedicamentoComItemGrupoMdto(
					fichaFarmaco.getMedicamento().getMatCodigo(),
					Boolean.TRUE);

			StringBuffer retorno = new StringBuffer(20);
			if(grupoMedicamento != null){
				retorno.append(getAmbulatorioFacade()
						.obterDescricaoCidCapitalizada(
								grupoMedicamento.getDescricao(),
								CapitalizeEnum.TODAS));
			}else{
				retorno.append(getAmbulatorioFacade()
						.obterDescricaoCidCapitalizada(
								fichaFarmaco.getMedicamento().getDescricao(),
								CapitalizeEnum.TODAS));
			}
			retorno.append(fichaFarmaco.getConcentracaoSiglaInfusao());

			if(fichaFarmaco.getPermaneceNoPos() != null && fichaFarmaco.getPermaneceNoPos()){
				retorno.append(" - Permanece no Pós");
			}

			return retorno.toString();
		}
		return null;
	}
	
	/**
	 * @ORADB MBCC_GET_TECNICA
	 * @param seqMbcFichaAnestesia
	 */
	public String mbccGetTecnica(Long seqMbcFichaAnestesia){
		List<MbcFichaTecnicaEspecial> fichasEspeciais = getBlocoCirurgicoFacade().pesquisarMbcFichasTecnicasEspeciaisComTecnicaEspecial(seqMbcFichaAnestesia);
		StringBuilder ret = new StringBuilder();
		for(int i =0; i < fichasEspeciais.size(); i++){
			MbcFichaTecnicaEspecial fichaEsp = fichasEspeciais.get(i);
			ret.append(fichaEsp.getMbcTecnicaEspeciais().getDescricao());
			ret.append(fichaEsp.getTempoAplicacaoEmHorasEMinutos());
			if(i+1 != fichasEspeciais.size()){
				ret.append(PONTO_VIRGULA____);
			}
		}
		
		return ret.toString();
	}
	
	/**
	 * @ORADB mbcc_get_conselho
	 * @return
	 */
	public String mbccGetConselho(Integer matriculaAnest, Short vinCodigoAnest){
		RapConselhosProfissionais conselhoProfissional = getCadastrosBasicosFacade().obterConselhoProfissionalComNroRegConselho(matriculaAnest, vinCodigoAnest, DominioSituacao.A);
		return conselhoProfissional != null ? conselhoProfissional.getSigla() : "";
	}
	
	/**
	 * @ORADB MBCC_GET_NRO_CONSELH
	 * @param matricula
	 * @param viNCodigo
	 * @return
	 */
	public String mbccGetNroConselh(Integer matricula, Short vinCodigo){
		return getCadastrosBasicosFacade().obterRapQualificaoComNroRegConselho(matricula, vinCodigo, DominioSituacao.A);
	}
	
	/**
	 * @ORADB MBCC_GET_MEDIC_PRE
	 * @param seqMbcFichaAnest
	 * Retorna todos os itens de monitorização definidos para a ficha.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String mbccGetMedicPre(Long seqMbcFichaAnest){
		List<ItemMonitorizacaoDefinidoFichaAnestVO> fichasAnestesias = getBlocoCirurgicoFacade().pesquisarItensMonitorizacaoDefinidosFicha(seqMbcFichaAnest);
		StringBuilder txt = null;
		
		for(ItemMonitorizacaoDefinidoFichaAnestVO item : fichasAnestesias){
			
			if(txt == null){
				txt = new StringBuilder();
			}else{
				txt.append(PONTO_VIRGULA____);
			}
			
			AfaGrupoMedicamento grupoMedicamento = getFarmaciaFacade().obterAfaGrupoMedicamentoComItemGrupoMdto(item.getMatCodigoMedicamento(), Boolean.TRUE);
			
			if(grupoMedicamento != null){
				txt.append(grupoMedicamento.getDescricao());
			}else{
				txt.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada(item.getDescricaoMedicamento(), CapitalizeEnum.TODAS));
			}
			
			txt.append(item.getSiglaViaAdministracao() != null ? item.getSiglaViaAdministracao() + " - " : "");
			
			txt.append(item.getDoseMbcFichaMedicPreAnest() != null ? item.getDoseMbcFichaMedicPreAnest() : "");
			/**
			 * //Verificar formatação da dose
			 * ||' - '||DECODE(TO_CHAR(fmn.dose),TRUNC(fmn.dose),TO_CHAR(fmn.dose), -- se é inteiro
               TO_CHAR(fmn.dose,'fm9999999990D00')) -- se tem decimal formata
			 */
			
			if(item.getSeqUnidadeMedidaMedica() != null){
				txt.append(item.getDescricaoUnidadeMedidaMedica());
			}else{
				txt.append(item.getDescricaoAfaTipoApresMdto() != null ? item.getDescricaoAfaTipoApresMdto() : "");
			}
		}
		
		return (txt != null && !"null".equals(txt.toString()))? txt.toString() : null;
	}
	
	/**
	 * @ORADB MBCC_GET_FLUIDO_ADM
	 * @param seqMbcFichaAnest
	 * @param tipoFluidoPerdido
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String mbccGetFluidoAdm(Long seqMbcFichaAnest, DominioTipoFluidoAdministrado tipoFluidoPerdido){
		List<MbcFichaFluidoAdministrados> fichasFluidos = getBlocoCirurgicoFacade().pesquisarMbcFichaFluidoAdministrado(seqMbcFichaAnest, tipoFluidoPerdido);
		StringBuilder txt = null;
		for(MbcFichaFluidoAdministrados fichaFluidoAdm : fichasFluidos){
			if(txt == null){
				txt = new StringBuilder();
			}else{
				txt.append(PONTO_VIRGULA____);
			}
			//OPEN c_per(
			MbcOcorrFichaFluidoAdms ocorrFichaFluido = getBlocoCirurgicoFacade().obterMbcOcorrFichaFluidoAdm(fichaFluidoAdm.getSeq(), DominioOcorrFichaFluido.F, Boolean.TRUE);
			//OPEN c_grupo(
			AfaGrupoMedicamento grupoMedicamento = null;
			if(fichaFluidoAdm.getFluidoAdministrado().getMedicamento() != null){
				grupoMedicamento = getFarmaciaFacade().obterAfaGrupoMedicamentoComItemGrupoMdto(fichaFluidoAdm.getFluidoAdministrado().getMedicamento().getMatCodigo(), Boolean.TRUE);
			}
			
			if(grupoMedicamento != null){
				txt.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada(grupoMedicamento.getDescricao(), CapitalizeEnum.TODAS));
			}else{
				String desc = null;
				if(fichaFluidoAdm.getFluidoAdministrado().getMedicamento() != null){
					desc = fichaFluidoAdm.getFluidoAdministrado().getMedicamento().getDescricao();
				}else{if(fichaFluidoAdm.getFluidoAdministrado().getComponenteSanguineo() != null){
					desc = fichaFluidoAdm.getFluidoAdministrado().getComponenteSanguineo().getDescricao();
				}else{
					desc = fichaFluidoAdm.getFluidoAdministrado().getProcedEspecialDiverso().getDescricao();
				}}
				//mpmc_minusculo(NVL(med.descricao, NVL(csa.descricao, ped.descricao)),2) descricao,
				desc = getAmbulatorioFacade().obterDescricaoCidCapitalizada(desc, CapitalizeEnum.TODAS);
				txt.append(desc);
			}
			
			if(fichaFluidoAdm.getObservacao() != null){
				txt.append(" - ");
				txt.append(fichaFluidoAdm.getObservacao());
			}
			
			if(fichaFluidoAdm.getVolumeTotal() != null){
				txt.append(" (");
				txt.append(fichaFluidoAdm.getVolumeTotal());
				txt.append(" mL)");
			}
			
			if(ocorrFichaFluido != null && ocorrFichaFluido.getPermaneceNoPos() != null && ocorrFichaFluido.getPermaneceNoPos()){
				txt.append(" - Permanece no Pós");
			}
			
		}
		//O retorno na function original é 0, adaptado para retorno null
		return txt != null ? txt.toString() : null;
		
	}
	/**
	 * @ORABDB MBCC_GET_FLUIDO_PERD
	 * @param seqMbcFichaAnest
	 * @param tipoFluidoAdministrado
	 * @return
	 */
	public String mbccGetFluidoPerd(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido){
		List<MbcFichaFluidoPerdido> fichaFluidoPedido = getBlocoCirurgicoFacade().pesquisarFichasFluidosPerdidosByFichaAnestesia(seqMbcFichaAnest, tipoFluidoPerdido);
		StringBuilder txt = null;
		
		for(MbcFichaFluidoPerdido fdd : fichaFluidoPedido){
			
			if(txt == null){
				txt = new StringBuilder();
			}else{
				txt.append(PONTO_VIRGULA____);
			}
			
			txt.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada(fdd.getMbcFluidosPerdidos().getDescricao(), CapitalizeEnum.TODAS));
			
			if(fdd.getObservacao() != null){
				txt.append(" - ");
				txt.append(fdd.getObservacao());
			}
			
			if(fdd.getVolumeTotal() != null){
				txt.append(" (");
				txt.append(fdd.getVolumeTotal());
				txt.append(" mL)");
			}
			
		}
		//O retorno na function original é 0, adaptado para retorno null
		return txt != null ? txt.toString() : null;
	}
	
	/**
	 * @ORADB MBCC_GET_TOT_PERDIDO
	 * @param seqMbcFichaAnest
	 * @param tipoFluidoPerdido
	 * @return
	 */
	public Long mbccGetTotPerdido(Long seqMbcFichaAnest, DominioTipoFluidoPerdido tipoFluidoPerdido){
		return getBlocoCirurgicoFacade().obterSomaVolumeTotalFluidoPerdido(seqMbcFichaAnest, tipoFluidoPerdido);
	}
	
	/**
	 * @ORADB MBCC_GET_ISQUEMIA_FR
	 * @param seqMbcFichaAnest
	 * @param seqMbcOrgaoTransplantado
	 * @return
	 */
	public String mbccGetIsquemiaFr(Long seqMbcFichaAnest, Short seqMbcOrgaoTransplantado){
		MbcFichaOrgaoTransplante fichaOrgao = getBlocoCirurgicoFacade().obterFichasOrgaosTransplantes(seqMbcFichaAnest, seqMbcOrgaoTransplantado);
	
		if(fichaOrgao != null && fichaOrgao.getDthrRetirada() != null){
			MbcOcorrenciaFichaEvento ocorrencia = getBlocoCirurgicoFacade().obterOcorrenciaFichaEvento(seqMbcFichaAnest, seqMbcOrgaoTransplantado, DominioOcorrenciaFichaEvento.I);
			if(ocorrencia == null){
				return null;
			}
			BigDecimal horas = DateUtil.calcularDiasEntreDatasComPrecisao(ocorrencia.getDthrOcorrencia(), fichaOrgao.getDthrRetirada()).multiply(new BigDecimal("24")).setScale(0, RoundingMode.HALF_UP);
			BigDecimal minutBig = DateUtil.calcularDiasEntreDatasComPrecisao(ocorrencia.getDthrOcorrencia(), fichaOrgao.getDthrRetirada()).multiply(new BigDecimal("24")).multiply(new BigDecimal("60")).setScale(0, RoundingMode.HALF_UP);
			Integer minut = minutBig.intValue() % 60;
			return horas.toString() + ":" + minut;
		}
		return null;
	}
	
	/**
	 * @ORADB MBCC_GET_ISQUEMIA_GT
	 * @param seqMbcFichaAnest
	 * @param seqMbcOrgaoTransplantado
	 * @return
	 */
	public String mbccGetIsquemiaGt(Long seqMbcFichaAnest, Short seqMbcOrgaoTransplantado){
		MbcOcorrenciaFichaEvento fichaEvento = getBlocoCirurgicoFacade().obterOcorrenciaFichaEvento(seqMbcFichaAnest, seqMbcOrgaoTransplantado, DominioOcorrenciaFichaEvento.I);
		
		if(fichaEvento != null && fichaEvento.getDthrOcorrencia() != null){
			MbcOcorrenciaFichaEvento ocorrencia = getBlocoCirurgicoFacade().obterOcorrenciaFichaEvento(seqMbcFichaAnest, seqMbcOrgaoTransplantado, DominioOcorrenciaFichaEvento.F);
			if(ocorrencia == null){
				return null;
			}
			
			BigDecimal horas = DateUtil.calcularDiasEntreDatasComPrecisao(ocorrencia.getDthrOcorrencia(), fichaEvento.getDthrOcorrencia()).multiply(new BigDecimal("24")).setScale(0, RoundingMode.HALF_UP);
			BigDecimal minutBig = DateUtil.calcularDiasEntreDatasComPrecisao(ocorrencia.getDthrOcorrencia(), fichaEvento.getDthrOcorrencia()).multiply(new BigDecimal("24")).multiply(new BigDecimal("60")).setScale(0, RoundingMode.HALF_UP);
			Integer minut = minutBig.intValue() % 60;
			return horas.toString() + ":" + minut;
		}
		return null;
	}
	
	/**
	 * @ORADB MCOC_PESO_RN
	 * @param codPaciente
	 * @return
	 */
	public AipPesoPacientes mcocPesoRn(Integer codPaciente){
		return getPacienteFacade().obterPesoPaciente(codPaciente, DominioMomento.N);
	}
	
	/**
	 * Retorna soma total de fluido/sangue perdido cfme param.
	 * @ORADB MBCC_GET_TOT_ADM
	 * @return
	 */
	public Long mbccGetTotAdm(Long seqMbcFichaAnest, Boolean agrupaTipoFluidoAdm){
		return getBlocoCirurgicoFacade().obterSomaVolumeTotalFluidoAdministrado(seqMbcFichaAnest, agrupaTipoFluidoAdm);
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	public ICadastrosBasicosFacade getCadastrosBasicosFacade(){
		return cadastrosBasicosFacade;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return blocoCirurgicoFacade;
	}
	
	public IFarmaciaFacade getFarmaciaFacade (){
		return this.farmaciaFacade;
	}
}
