package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioSolicitacaoExamesCertificacaoDigitalON extends BaseBusiness{


@EJB
private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;

private static final Log LOG = LogFactory.getLog(RelatorioSolicitacaoExamesCertificacaoDigitalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6009646992215319889L;

	public List<TicketExamesPacienteVO> pesquisarRelatorioSolicitacaoExame(
			Integer codSolicitacao)  throws ApplicationBusinessException {
		
		AelSolicitacaoExames solicitacao = getAelSolicitacaoExameDAO().obterPeloId(codSolicitacao);
		
		Boolean servidorHabilitado = getCertificacaoDigitalFacade().verificarServidorHabilitadoCertificacaoDigital(solicitacao.getServidor());
		
		if(servidorHabilitado){
			
			AghUnidadesFuncionais unidadeFuncionalColeta = getRelatorioTicketExamesPacienteON().buscarUnidadeFuncionalColeta();
			List<AelItemSolicitacaoExames> solicitacoes = getAelSolicitacaoExameDAO().pesquisarRelatorioTicketExamesPaciente(codSolicitacao, false, null, null);
			
			List<TicketExamesPacienteVO> result = new ArrayList<TicketExamesPacienteVO>();
			
			for(AelItemSolicitacaoExames itemSolicitacao : solicitacoes){
				TicketExamesPacienteVO vo = new TicketExamesPacienteVO();
				
				AelSolicitacaoExames solicitacaoExames = itemSolicitacao.getSolicitacaoExame();
				RapServidores servidorResponsavel = solicitacaoExames.getServidorResponsabilidade();
				AghAtendimentos atendimento = solicitacaoExames.getAtendimento();
				FatConvenioSaudePlano convenioSaudePlano = solicitacaoExames.getConvenioSaudePlano();
				AelUnfExecutaExames executaExames = itemSolicitacao.getAelUnfExecutaExames();
				AelExamesMaterialAnalise examesMaterialAnalise = executaExames.getAelExamesMaterialAnalise();
				AelMateriaisAnalises materialAnalise = examesMaterialAnalise.getAelMateriaisAnalises();
				AelExames aelExames = examesMaterialAnalise.getAelExames();
				
				AghCaractUnidFuncionais caractUnidFuncionais = 
					getAghuFacade().buscarCaracteristicaPorUnidadeCaracteristica(executaExames.getId().getUnfSeq().getSeq(),ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
				
				if(caractUnidFuncionais != null && !caractUnidFuncionais.getId().getCaracteristica().equals(ConstanteAghCaractUnidFuncionais.AREA_FECHADA)){
					caractUnidFuncionais = null;
				}
				
				vo.setProntuario(getRelatorioTicketExamesPacienteON().buscarLaudoProntuarioPaciente(solicitacaoExames).toString());
				vo.setNome(getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(solicitacaoExames));
				vo.setSolicitacaoExameSeq(solicitacaoExames.getSeq().toString());
				
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				vo.setCriadoEm(sdf1.format(solicitacaoExames.getCriadoEm()));
				
				Date dtNasc = getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(solicitacaoExames);
				sdf1 = new SimpleDateFormat("dd/MM/yyyy");
				vo.setNascimento(sdf1.format(dtNasc));
				
				vo.setIdade(DateUtil.obterIdadeFormatadaAnoMesDia(dtNasc));
				if(servidorResponsavel !=null){
					vo.setNomeResp(getRelatorioTicketExamesPacienteON().buscarNomeServ(servidorResponsavel.getId().getMatricula(),servidorResponsavel.getId().getVinCodigo()));
				}
				vo.setDescricaoUnidadeSolicitante(solicitacaoExames.getUnidadeFuncional().getDescricao());
				
				// CONVENIO
				//aelc_busca_conv_plan(DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_cnv_codigo),DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_seq)),
				String trecho1 = getRelatorioTicketExamesPacienteON().buscarConvenioPlano(atendimento == null ? null : convenioSaudePlano);
				String trecho2 = " /  ";
				//substr(aelc_busca_conv_plan(soeVAS.csp_cnv_codigo,soeVAS.csp_seq),1,instr(aelc_busca_conv_plan(soeVAS.csp_cnv_codigo,soeVAS.csp_seq),'/')-1),
				String argTrecho3 = getRelatorioTicketExamesPacienteON().buscarConvenioPlano(convenioSaudePlano);
				String trecho3 = argTrecho3.substring(0, argTrecho3.indexOf('/')-1);
				//aelc_busca_conv_plan(DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_cnv_codigo) ,DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_seq))
				String trecho4 = trecho1;
				vo.setConvenio(trecho1 == trecho2 ? trecho3 : trecho4);
				
				vo.setDescricaoMaterial(materialAnalise.getDescricao());
				vo.setItemSolicitacaoExameSeqP(itemSolicitacao.getId().getSeqp().toString());
				vo.setDescricaoUsual(aelExames.getDescricaoUsual());
				
				if(caractUnidFuncionais != null && caractUnidFuncionais.getUnidadeFuncional() != null){
					vo.setUnfSeq(caractUnidFuncionais.getUnidadeFuncional().getSeq().toString());
				}else{
					vo.setUnfSeq(unidadeFuncionalColeta.getSeq().toString());
				}
				
				vo.setInformacoesClinicas(getRelatorioTicketExamesPacienteON().buscarInformacoesClinicas(solicitacaoExames.getInformacoesClinicas(),vo.getUnfSeq()));
				
				vo.setTextoComparecer(getRelatorioTicketExamesPacienteON().buscarTextoComparecer(itemSolicitacao,caractUnidFuncionais));
				
				
				result.add(vo);
			}
			
			return result;
			
		}else {
			return null;
		}
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON(){
		return relatorioTicketExamesPacienteON;
	}
	
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.certificacaoDigitalFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
