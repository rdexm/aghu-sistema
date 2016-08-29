package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaAntecedenteId;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaSumarioRN.ManterAltaSumarioRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtCausaAntecedenteON extends BaseBusiness {


@EJB
private ManterObtCausaAntecedenteRN manterObtCausaAntecedenteRN;

private static final Log LOG = LogFactory.getLog(ManterObtCausaAntecedenteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmObtCausaAntecedenteDAO mpmObtCausaAntecedenteDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6562744618678552767L;

	/**
	 * Cria uuma cópia de ObtCausaAntecedente
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarObtCausaAntecedente(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmObtCausaAntecedente> listaObtCausaAntecedente = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmObtCausaAntecedente obtCausaAntecedente : listaObtCausaAntecedente) {
			
			MpmObtCausaAntecedente novoObtCausaAntecedente = new MpmObtCausaAntecedente();
			novoObtCausaAntecedente.setMpmAltaSumarios(altaSumario);
			novoObtCausaAntecedente.setCid(obtCausaAntecedente.getCid());
			novoObtCausaAntecedente.setComplCid(obtCausaAntecedente.getComplCid());
			novoObtCausaAntecedente.setDescCid(obtCausaAntecedente.getDescCid());
			novoObtCausaAntecedente.setDiagnostico(obtCausaAntecedente.getDiagnostico());
			novoObtCausaAntecedente.setIndCarga(obtCausaAntecedente.getIndCarga());
			novoObtCausaAntecedente.setIndSituacao(obtCausaAntecedente.getIndSituacao());
			novoObtCausaAntecedente.setMpmCidAtendimentos(obtCausaAntecedente.getMpmCidAtendimentos());
			novoObtCausaAntecedente.setPrioridade(obtCausaAntecedente.getPrioridade());
			this.getManterObtCausaAntecedenteRN().inserirObtCausaAntecedente(novoObtCausaAntecedente);
			
		}
		
	}	

	public String persistirCausaAntecedente(
			SumarioAltaDiagnosticosCidVO itemSelecionado) throws ApplicationBusinessException {		
		if(itemSelecionado.getObitoCausaAntecendente() != null && itemSelecionado.getObitoCausaAntecendente().getId() != null){
			preAtualizarCausaAntecedente(itemSelecionado);
			return "MENSAGEM_SUCESSO_ALTERACAO_CAUSA_ANTECEDENTE_MORTE";
		}else{
			preInserirCausaAntecedente(itemSelecionado);
			return "MENSAGEM_SUCESSO_INCLUSAO_CAUSA_ANTECEDENTE_MORTE";
		}
	}
	
	private void preAtualizarCausaAntecedente(
			SumarioAltaDiagnosticosCidVO itemSelecionado) throws ApplicationBusinessException{
		MpmObtCausaAntecedente mpmObtCausaAntecedenteEdicao = itemSelecionado.getObitoCausaAntecendente();
		if(mpmObtCausaAntecedenteEdicao.getIndCarga().equals(DominioSimNao.S)){
			throw new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.ERRO_ALTERAR_REGISTRO_OBITO_CAUSA_ANTECEDENTE);
		}else{
			mpmObtCausaAntecedenteEdicao.setComplCid(itemSelecionado.getComplementoEditado());
			mpmObtCausaAntecedenteEdicao.setCid(itemSelecionado.getCid());
			mpmObtCausaAntecedenteEdicao.setPrioridade(itemSelecionado.getPrioridade());
			this.getMpmObtCausaAntecedenteDAO().atualizar(mpmObtCausaAntecedenteEdicao);
			this.getMpmObtCausaAntecedenteDAO().flush();
		}
	}

	private void preInserirCausaAntecedente(
			SumarioAltaDiagnosticosCidVO obtCausaAntecedente) throws ApplicationBusinessException {		
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(obtCausaAntecedente.getId().getApaAtdSeq(),obtCausaAntecedente.getId().getApaSeq(),obtCausaAntecedente.getId().getSeqp());
		if(altaSumario != null){
			MpmObtCausaAntecedente mpmObtCausaAntecedenteNovo = new MpmObtCausaAntecedente();
			MpmObtCausaAntecedenteId id = new MpmObtCausaAntecedenteId();
			id.setAsuApaAtdSeq(altaSumario.getId().getApaAtdSeq());
			id.setAsuApaSeq(altaSumario.getId().getApaSeq());
			id.setAsuSeqp(altaSumario.getId().getSeqp());
			
			mpmObtCausaAntecedenteNovo.setMpmAltaSumarios(altaSumario);
			
			this.getMpmObtCausaAntecedenteDAO().obterValorSequencialId(mpmObtCausaAntecedenteNovo);

			mpmObtCausaAntecedenteNovo.setId(id);
			
			mpmObtCausaAntecedenteNovo.setIndSituacao(DominioSituacao.A);

			if(obtCausaAntecedente.getCiaSeq() != null){
				mpmObtCausaAntecedenteNovo.setIndCarga(DominioSimNao.S);
			}else{
				mpmObtCausaAntecedenteNovo.setIndCarga(DominioSimNao.N);
			}
			mpmObtCausaAntecedenteNovo.setDescCid(ManterAltaSumarioON.montaDescricaoCIDComPrimeiraMaiuscula(obtCausaAntecedente.getCid().getDescricao(), obtCausaAntecedente.getCid().getCodigo()));
			mpmObtCausaAntecedenteNovo.setCid(obtCausaAntecedente.getCid());
			mpmObtCausaAntecedenteNovo.setComplCid(obtCausaAntecedente.getComplementoEditado());
			List<MpmObtCausaAntecedente> list = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
			if(list != null){
				mpmObtCausaAntecedenteNovo.setPrioridade((short)(list.size()+1));
			}else{
				mpmObtCausaAntecedenteNovo.setPrioridade((short)1);
			}
			
			this.getManterObtCausaAntecedenteRN().inserirObtCausaAntecedente(mpmObtCausaAntecedenteNovo);
		}
		
	}
	
	/**
	 * Remove ObtCausaAntecedente
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void removerObtCausaAntecedente(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmObtCausaAntecedente> listaObtCausaAntecedente = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);
		
		for (MpmObtCausaAntecedente obtCausaAntecedente : listaObtCausaAntecedente) {
			this.getManterObtCausaAntecedenteRN().removerObtCausaAntecedente(obtCausaAntecedente);
		}
	}
	
	public void removerObtCausaAntecedente(SumarioAltaDiagnosticosCidVO itemGrid) throws ApplicationBusinessException{
		
		MpmObtCausaAntecedente mpmObtCausaAntecedente =  this.getMpmObtCausaAntecedenteDAO().obterPorChavePrimaria(itemGrid.getObitoCausaAntecendente().getId());
		this.getMpmObtCausaAntecedenteDAO().remover(mpmObtCausaAntecedente);
		this.getMpmObtCausaAntecedenteDAO().flush();
	}
	
	protected MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO() {
		return mpmObtCausaAntecedenteDAO;
	}
	
	protected ManterObtCausaAntecedenteRN getManterObtCausaAntecedenteRN() {
		return manterObtCausaAntecedenteRN;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO(){
		return mpmAltaSumarioDAO;
	}
	
}
