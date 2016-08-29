package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.exames.business.AghAtendimentosPacExternRN.AghAtendimentosPacExternRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AghAtendimentosPacExternDAO;
import br.gov.mec.aghu.exames.dao.AghMedicoExternoDAO;
import br.gov.mec.aghu.exames.vo.AtendimentoExternoVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;

@Stateless
public class AtendimentoExternoON extends BaseBusiness {


@EJB
private AghAtendimentosPacExternRN aghAtendimentosPacExternRN;

private static final Log LOG = LogFactory.getLog(AtendimentoExternoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@Inject
private AghMedicoExternoDAO aghMedicoExternoDAO;

@Inject
private AghAtendimentosPacExternDAO aghAtendimentosPacExternDAO;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6993056491613406761L;

	public List<AghMedicoExterno> obterMedicoExternoList(String parametro) {
		return getAghMedicoExternoDAO().obterMedicoExternoList(parametro);
	}
	
	public Long obterMedicoExternoListCount(String parametro) {
		return getAghMedicoExternoDAO().obterMedicoExternoListCount(parametro);
	}
	
	
	
	public Integer listarConvenioSaudePlanosCount(String parametro) {
		List<FatConvenioSaudePlano> lista = this.listarConvenioSaudePlanos(parametro, null);
		return lista.size();
	}
	
	public List<FatConvenioSaudePlano> listarConvenioSaudePlanos(String parametro) {
		return this.listarConvenioSaudePlanos(parametro, 200);
	}
	
	/**
	 * Se o parametro estiver separado por espaco " " entao realiza pesquisa pela PK de FatConvenioSaudePlano.
	 * 
	 * @param parametro
	 * @return
	 */
	private FatConvenioSaudePlano getListarConvenioSaudePlano(String parametro) {
		FatConvenioSaudePlano convenio = null;
		
		Short cnvCodigo = null;
		Byte seqp = null;
		String[] pk = parametro.split(" ");
		if (pk.length > 1) {
			if (CoreUtil.isNumeroShort(pk[0])) {
				cnvCodigo = Short.valueOf(pk[0]);
			}
			if (CoreUtil.isNumeroByte(pk[1])) {
				seqp = Byte.valueOf(pk[1]);
			}
		}
		
		// Se tiver um cnvCodigo e um seq (pk) efetuar busca abaixo.
		if (cnvCodigo != null && seqp != null) {
			convenio = getFaturamentoFacade().obterConvenioSaudePlano(cnvCodigo, seqp);			
		}
		
		return convenio;
	}
	
	private List<FatConvenioSaudePlano> listarConvenioSaudePlanos(String parametro, Integer maxResult) {
		FatConvenioSaudePlano convenio = this.getListarConvenioSaudePlano(parametro);
		
		List<FatConvenioSaudePlano> lista;
		if (convenio != null) {
			// Encontrou convenio pela PK.
			lista = new LinkedList<FatConvenioSaudePlano>();
			lista.add(convenio);
		} else {
			// busca convenio pela descricao.
			lista = getFaturamentoFacade().listarConvenioSaudePlanos(parametro);
			if (maxResult != null && maxResult.intValue() > 0 && lista.size() > maxResult) {
				lista = lista.subList(0, maxResult);
			}
		}
		
		return lista;
	}

	
	
	public List<AtendimentoExternoVO> obterAtendimentoExternoList(Integer codigoPaciente) {
		if (codigoPaciente == null) {
			throw new IllegalArgumentException("Codigo do paciente nao informado!!!");
		}
		List<AghAtendimentosPacExtern> atendimentoPacExternoList = 
			this.getAghAtendimentosPacExternDAO().listarPorPaciente(codigoPaciente);
			//this.getAghuFacade().listarAtendimentosPacExternPorCodigoPaciente(codigoPaciente);
		
		List<AtendimentoExternoVO> antedimentoExternoVOList = new LinkedList<AtendimentoExternoVO>();
		for (AghAtendimentosPacExtern atendPacExternEl : atendimentoPacExternoList) {
			AtendimentoExternoVO atendimentoExternoVo = new AtendimentoExternoVO(atendPacExternEl);
			
			Boolean permitAmbula = Boolean.FALSE;
			if(atendimentoExternoVo.getAtendimento() != null){
				permitAmbula = !getAmbulatorioFacade()
						.pesquisarAtendimentoParaPrescricaoMedica(
								null,atendimentoExternoVo.getAtendimento().getSeq())
								.isEmpty();
			}
			
			atendimentoExternoVo.setAtendimentoPermitePrescricaoAmbulatorial(permitAmbula);
			
			antedimentoExternoVOList.add(atendimentoExternoVo);
		}
		
		return antedimentoExternoVOList;
	}
	
	public AghAtendimentosPacExtern gravar(AghAtendimentosPacExtern atendimentosPacExtern, String nomeMicrocomputador, RapServidores servidor) throws BaseException {
		this.verificarGravacao(atendimentosPacExtern);
		AghAtendimentosPacExtern umAghAtendimentosPacExtern = null;
		
		if (atendimentosPacExtern.getSeq() == null) {
			umAghAtendimentosPacExtern = this.getAghAtendimentosPacExternRN().inserir(atendimentosPacExtern, nomeMicrocomputador, servidor);			
		} else {
			umAghAtendimentosPacExtern = this.getAghAtendimentosPacExternRN().alterar(atendimentosPacExtern, nomeMicrocomputador, servidor);			
		}
		
		return umAghAtendimentosPacExtern;
	}
	
	/**
	 * Verificacoes necessarias para Insert e Update.<br>
	 * 
	 * @param atendimentosPacExtern
	 * @throws ApplicationBusinessException 
	 */
	private void verificarGravacao(AghAtendimentosPacExtern atendimentosPacExtern) throws ApplicationBusinessException {
		if (atendimentosPacExtern.getDtColeta() != null && DateUtil.validaDataMaior(atendimentosPacExtern.getDtColeta(), new Date())) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AEL_03066);
		}
	}

	private AghAtendimentosPacExternDAO getAghAtendimentosPacExternDAO() {
		return aghAtendimentosPacExternDAO;
	}

	/**	get/set **/
	protected AghMedicoExternoDAO getAghMedicoExternoDAO() {
		return aghMedicoExternoDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	protected AghAtendimentosPacExternRN getAghAtendimentosPacExternRN() {
		return aghAtendimentosPacExternRN;
	}


}
